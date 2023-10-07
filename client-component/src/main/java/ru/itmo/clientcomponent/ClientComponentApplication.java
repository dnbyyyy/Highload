package ru.itmo.clientcomponent;

import ru.itmo.clientcomponent.entities.KeyValue;
import ru.itmo.clientcomponent.service.ClientService;


public class ClientComponentApplication {

    public static void main(String[] args) {
        ClientService clientService = new ClientService();
        if(args[0].equals("get")){
            try{
                KeyValue keyValue = clientService.get(args[1]).block();
                System.out.println("Get Key Value Pair:" + keyValue.getKey() + " " + keyValue.getValue());
            } catch (Exception e){
                System.err.println("Error while getting Key Value pair:" + e.getMessage());
            } finally {
                System.out.println("-----");
            }
        } else if(args[0].equals("set")){
            try {
                clientService.set(args[1], args[2]).block();
                System.out.println("Set Key Value Pair");
            } catch (Exception e){
                System.err.println("Error while setting Key Value pair:" + e.getMessage());
            }
        } else{
            System.err.println("bad input: command not supported");
        }
    }
}