package edu.sjsu.cmpe.cache.client;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {
    private final String cacheServerUrl;

    public DistributedCacheService(String serverUrl) {
        this.cacheServerUrl = serverUrl;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
     */
    @Override
    
    public String get(long key) 
    {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
        String value = response.getBody().getObject().getString("value");

        return value;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
     *      java.lang.String)
     */
    @Override
    public void put(long key, String value) 
    {	
    	HttpResponse<JsonNode> response = null;
    	try 
    	{
    		response = Unirest
    				.put(this.cacheServerUrl + "/cache/{key}/{value}")
    				.header("accept", "application/json")
    				.routeParam("key", Long.toString(key))
    				.routeParam("value", value).asJson();
    	}
    	catch (UnirestException e) 
    	{
    		System.err.println(e);
    	}
    	if (response.getCode() != 200) 
    	{
    		System.out.println("Failed to add to the cache.");
    	}
    	
    }
    
    @Override
    public Future<HttpResponse<JsonNode>> getAsync(long key) 
    {
    	String value = null ;
    	Future<HttpResponse<JsonNode>> future = null;
    	try{
    	 
    		System.out.println("Inside getAsync() method for key :"+key);
    	 future = Unirest
    			.get(this.cacheServerUrl + "/cache/{key}")
  			  .header("accept", "application/json")
  			  .routeParam("key", Long.toString(key))
  			  .asJsonAsync(new Callback<JsonNode>() 
  			{
  			    public void failed(UnirestException e)
  			    {
  			        System.out.println("The request has failed");
  			    }
  			    public void completed(HttpResponse<JsonNode> response)
  			    {
  			      System.out.println("Complted method");
  			    	int code = response.getCode();
  			    	//Map<String, String> headers = response.getHeaders();
  			    	JsonNode body = response.getBody();
  			    	InputStream rawBody = response.getRawBody(); 
  			    	final String value = response.getBody().getObject().getString("value");	
  			    	System.out.println("value is :"+ value);
  			    	
  			    }
  			    public void cancelled()
  			    {
  			        System.out.println("The request has been cancelled");
  			    }
  			 });
    	  //value = future.get().getBody().getObject().getString("value");
    	// System.out.println(future.get());
    	}
    	
         catch (Exception e1) {
			
			e1.printStackTrace();
		} 
    	return future; 
    }
    
    public HttpResponse<JsonNode> delete(long key) 
    {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.delete(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        return response;
    }
    
    @Override
    public int hashCode() {
    	return cacheServerUrl.hashCode();
    }

	@Override
	public Future<HttpResponse<JsonNode>> putAsync(long key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCacheServiceUrl() {
		// TODO Auto-generated method stub
		return this.cacheServerUrl;
	}
}
