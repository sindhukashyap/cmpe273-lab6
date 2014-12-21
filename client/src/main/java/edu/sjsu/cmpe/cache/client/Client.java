package edu.sjsu.cmpe.cache.client;


import java.util.List;


public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
        ClientHandler handler = new ClientHandler();
        List<CacheServiceInterface> serverList = handler.buildServers();
        CRDTClient client = new CRDTClient();
       //First put call
        client.putAsyncAll(serverList,1,"a");
        System.out.println("Sleeping after first put call...");
        Thread.sleep(30000);
        client.putAsyncAll(serverList,1,"b");
        System.out.println("Sleeping after second put call...");
        Thread.sleep(60000);
       //Get async call 
       client.asyncGetWithRepair(serverList,1);
       
    }

}
