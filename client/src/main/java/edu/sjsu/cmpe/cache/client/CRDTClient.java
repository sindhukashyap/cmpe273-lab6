package edu.sjsu.cmpe.cache.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

public class CRDTClient {
	public AtomicInteger successCount;
	
//    public Future<HttpResponse<JsonNode>> putAsync(long key, String value)
//    {
//    	Future<HttpResponse<JsonNode>> future = Unirest
//    			.put(this.cacheServerUrl + "/cache/{key}/{value}")
//  			  .header("accept", "application/json")
//  			  .routeParam("key", Long.toString(key))
//  			  .routeParam("value", value)
//  			  .asJsonAsync(new Callback<JsonNode>() 
//  			{
//  			    public void failed(UnirestException e)
//  			    {
//  			        System.out.println("The request has failed");
//  			    }
//
//  			  public void completed(HttpResponse<JsonNode> response)
//  			  {
//  		         int code = response.getCode();
//  		         //Map<String, String> headers = response.getHeaders();
//  		         JsonNode body = response.getBody();
//  		         InputStream rawBody = response.getRawBody();
//  		      }
//  			    public void cancelled()
//  			    {
//  			        System.out.println("The request has been cancelled");
//  			    }
//  			 });
//    	
//    	return future;
//    }
	
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
			// TODO Auto-generated catch block
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
	
//	public void putCall(List<CacheServiceInterface> serverList, long key, String value) throws InterruptedException, ExecutionException
//	{
//		 List<Future<HttpResponse<JsonNode>>> futureList = new ArrayList<Future<HttpResponse<JsonNode>>>();
//	        for(CacheServiceInterface cache : serverList)
//	        {
//	        	Future<HttpResponse<JsonNode>> future = cache.putAsync(key, value);
//	        	futureList.add(future);
//	            System.out.println("put(" + key + "=>" + value);
//	        }
////	        int successCount = 0;
////	        for (Future<HttpResponse<JsonNode>> fut : futureList)
////	        {	
////	        	HttpResponse<JsonNode> response = null;
////	        	try {
////	        		response = fut.get(15, TimeUnit.SECONDS);
////	        
////	        	} 
////	        	
////	        	catch(TimeoutException e) {
////	        		System.out.println("Connection timeout");
////	        	}
////	        	
////	        	
////	        	if (response != null) {
////	        		if(response.getCode() == 200)
////	        		{
////	        			successCount++;	
////	        		}
////	        		else {
////	        			System.out.println("Response code != 200");
////	        		}
////	        	}
////	        }
////	         if(successCount < 2)
////	         {
////	        	 System.out.println("Deleting from all three servers...");
////	        	 for(CacheServiceInterface cache : serverList)
////	        		 
////	             {
////	        		 System.out.println("Deleting from server.....");
////	        		 cache.delete(key); 
////	             }
////	         }
//	        
//	        
//	}
	
	public void asyncGetWithRepair(List<CacheServiceInterface> serverList, long key) throws JSONException, InterruptedException, ExecutionException
	{
		List<String> valueList = new ArrayList<String>(); 
		List<Future<HttpResponse<JsonNode>>> futureList = new ArrayList<Future<HttpResponse<JsonNode>>>();
		
		/*
		 for(CacheServiceInterface cache : serverList)
	        {
			 Future<HttpResponse<JsonNode>> future = cache.getAsync(1);
			 	System.out.println(future);
			 	
//			 	Callback<JsonNode> callback = getAsync(long key) ; 
//				callback.completed(future.get());
//			 	futureList.add(future);	
	        }
//		 for(int i = 0 ;i<valueList.size(); i++)
//		 {
//			 System.out.println("The values are :" + valueList.get(i)+ " ,");
////			 if(valueList.get(1).equals(valueList.get(2)) )
////			 {
////				 
////			 }
//		 }
		 
		 for (Future<HttpResponse<JsonNode>> fut : futureList)
		 {
			 Thread.sleep(1000);
			 boolean done = fut.isDone();
			 System.out.println("isDone() "+done + ",");
//			 String value = fut.get().getBody().getObject().getString("value");
//			 System.out.println("Get Value is "+value + ",");
			 //valueList.add(value);
			 System.out.println("future.get()"+fut.get());
		 }
		 */
		Map<String,CacheServiceInterface> serverMap = new HashMap<String,CacheServiceInterface>(); 
		
		CacheServiceInterface server1 = serverList.get(0);
		String value1 = server1.get(key);
		CacheServiceInterface server2 = serverList.get(1);
		String value2 = server2.get(key);
		CacheServiceInterface server3 = serverList.get(2);
		String value3 = server3.get(key);
		
		CacheServiceInterface minorityServer = null;
		CacheServiceInterface majorityServer = server1;
		if(value1.equals(value2) && !value1.equals(value3)) {
			minorityServer = server3;
//			majorityServer = server1;
		}
		else if(value1.equals(value3) && !value1.equals(value2)) {
			minorityServer = server2;
//			majorityServer = server1;
		}
		else if(value2.equals(value3) && !value2.equals(value1)) {
			minorityServer = server1;
			majorityServer = server2;
		}
		//else minorityServer = null for all equal
		if(minorityServer != null)
			minorityServer.put(key, majorityServer.get(key));
		
		
		
		
//		for(CacheServiceInterface cache : serverList)
//        {
//			String value = cache.get(1);
//			serverMap.put(value, cache);
//			int count = 0;
//			
//        }
//		for(Map.Entry<String,CacheServiceInterface> entry : serverMap.entrySet())
//		{
//			String key = entry.getKey();
//			
//		}
		
		
		
		
		
		
        
	}

}
