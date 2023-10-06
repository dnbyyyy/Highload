package ru.itmo.clientcomponent;

import ru.itmo.clientcomponent.service.ClientService;


public class ClientComponentApplication {

    public static void main(String[] args) {
        ClientService clientService = new ClientService();
        clientService.set("123","1");
        clientService.get("123").subscribe(keyValue -> {
                        System.out.println("Key Value pair:" + keyValue.getKey() + " " + keyValue.getValue());
                    },
                    error -> {
                        System.err.println("Error while getting Key Value pair:" + error.getMessage());
                    });
        if (args[0].equals("get")) {
            clientService.get(args[1]).subscribe(
                    keyValue -> {
                        System.out.println("Key Value pair:" + keyValue.getKey() + " " + keyValue.getValue());
                    },
                    error -> {
                        System.err.println("Error while getting Key Value pair:" + error.getMessage());
                    }
            );
        } else if (args[0].equals("set")) {
            clientService.set(args[1], args[2]).subscribe(
                    success -> {
                    },
                    error -> {
                        System.err.println("Error while setting Key Value pair:" + error.getMessage());
                    },
                    () -> {
                        System.out.println("Set Key Value pair");
                    }
            );
        } else {
            System.err.println("bad input: command not supported");
        }

    }
}


