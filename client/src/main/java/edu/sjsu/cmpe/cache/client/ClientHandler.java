package edu.sjsu.cmpe.cache.client;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	
	public ClientHandler()
	{
		
	}
	
	public List<CacheServiceInterface> buildServers()
	{
		List<CacheServiceInterface> serverList = new ArrayList<CacheServiceInterface>(); 
		CacheServiceInterface cache1 = new DistributedCacheService("http://localhost:3000"); 
		CacheServiceInterface cache2 = new DistributedCacheService("http://localhost:3001"); 
		CacheServiceInterface cache3 = new DistributedCacheService("http://localhost:3002");
		serverList.add(cache1);
		serverList.add(cache2);
		serverList.add(cache3);
		return serverList;
	}
	
}
