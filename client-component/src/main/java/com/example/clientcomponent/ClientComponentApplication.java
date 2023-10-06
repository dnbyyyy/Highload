package com.example.clientcomponent;

import com.example.clientcomponent.service.ClientService;

import java.util.Scanner;


public class ClientComponentApplication {

    public static void main(String[] args) {
        ClientService clientService = new ClientService();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                break;
            }

            String[] inputs = input.split(" ");
            if (inputs[0].equals("get")) {
                clientService.get(inputs[1]).subscribe(
                        keyValue -> {
                            System.out.println("Key Value pair:" + keyValue.getKey() + " " + keyValue.getValue());
                        },
                        error -> {
                            System.err.println("Error while getting Key Value pair:" + error.getMessage());
                        }
                );
            } else if (inputs[0].equals("set")) {
                clientService.set(inputs[1], inputs[2]).subscribe(
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

}
