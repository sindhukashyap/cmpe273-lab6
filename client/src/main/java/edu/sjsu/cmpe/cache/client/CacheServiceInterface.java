package edu.sjsu.cmpe.cache.client;

import java.util.concurrent.Future;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

/**
 * Cache Service Interface
 * 
 */
public interface CacheServiceInterface {
    public String get(long key);

    public void put(long key, String value);

    public Future<HttpResponse<JsonNode>> putAsync(long key, String value);
    
    public HttpResponse<JsonNode> delete(long key);
    
    public String getCacheServiceUrl();
}
