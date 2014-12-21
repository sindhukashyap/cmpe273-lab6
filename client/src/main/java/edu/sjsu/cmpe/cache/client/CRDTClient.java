package edu.sjsu.cmpe.cache.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

public class CRDTClient {
	public AtomicInteger successCount;
	
	public void putAsyncAll(List<CacheServiceInterface> serverList, long key, String value) {
		final CountDownLatch countDownLatch = new CountDownLatch(serverList.size());
        successCount = new AtomicInteger();
        
        for (final CacheServiceInterface server : serverList) {
        	Future<HttpResponse<JsonNode>> future = Unirest.put(server.getCacheServiceUrl() + "/cache/{key}/{value}")
      			  .header("accept", "application/json")
      			  .routeParam("key", Long.toString(key))
      			  .routeParam("value", value)
      			  .asJsonAsync(new Callback<JsonNode>() 
      			{
      			    public void failed(UnirestException e)
      			    {
      			        System.out.println("The request has failed");
      			        countDownLatch.countDown();
      			    }

      			  public void completed(HttpResponse<JsonNode> response)
      			  {
      		         countDownLatch.countDown();
      		         int code = response.getCode();
      		         if (code == 200) {
                         successCount.incrementAndGet();
                     }
      		         
      		      }
      			    public void cancelled()
      			    {
      			        System.out.println("The request has been cancelled");
      			    }
      			 });
        }
        try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        if(successCount.intValue() < 2)
        {
       	 System.out.println("Deleting from all three servers...");
       	 	for(CacheServiceInterface cache : serverList)
            {
	       		 System.out.println("Deleting from server.....");
	       		 cache.delete(key); 
            }
        }
	}
	
	public Map<CacheServiceInterface,String> getAsyncAll(List<CacheServiceInterface> serverList, long key) 
	{
		final CountDownLatch countDownLatch = new CountDownLatch(serverList.size());
        successCount = new AtomicInteger();
        final Map<CacheServiceInterface,String> serverMap = new HashMap<CacheServiceInterface,String>(); 
        for (final CacheServiceInterface server : serverList) 
        {
        	Future<HttpResponse<JsonNode>> future = Unirest
        			.get(server.getCacheServiceUrl() + "/cache/{key}")
        			  .header("accept", "application/json")
        			  .routeParam("key", Long.toString(key))
        			  .asJsonAsync(new Callback<JsonNode>() 
      			{
      			    public void failed(UnirestException e)
      			    {
      			        System.out.println("The request has failed");
      			        countDownLatch.countDown();
      			    }

      			  public void completed(HttpResponse<JsonNode> response)
      			  {
      		         countDownLatch.countDown();
      		         int code = response.getCode();
      		         String value = response.getBody().getObject().getString("value");
      		         if (code == 200) {
                         successCount.incrementAndGet();
                         serverMap.put(server, value);
                     }
      		         
      		      }
      			    public void cancelled()
      			    {
      			        System.out.println("The request has been cancelled");
      			    }
      			 });
        }
        try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
		return serverMap;
		
	}
	
	public void asyncGetWithRepair(List<CacheServiceInterface> serverList, long key) throws JSONException, InterruptedException, ExecutionException
	{
		
		System.out.println("Repairing on Read");
		Map<CacheServiceInterface,String> serverValuesMap =  getAsyncAll(serverList,key);
		CacheServiceInterface server1 = serverList.get(0);
		String value1 = serverValuesMap.get(server1);
		CacheServiceInterface server2 = serverList.get(1);
		String value2 = serverValuesMap.get(server2);
		CacheServiceInterface server3 = serverList.get(2);
		String value3 = serverValuesMap.get(server3);
		
		CacheServiceInterface minorityServer = null;
		CacheServiceInterface majorityServer = server1;
		if(value3 == null && value1.equals(value2)) {
			minorityServer = server3;
		}
		else if(value2 == null && value1.equals(value3)) {
			minorityServer = server2;
		}
		else if(value1 == null && value2.equals(value3)) {
			minorityServer = server1;
			majorityServer = server2;
		}
		if(minorityServer != null)
			minorityServer.put(key, majorityServer.get(key));
		
	}

}
