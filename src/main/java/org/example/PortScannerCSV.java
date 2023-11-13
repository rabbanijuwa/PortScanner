package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import java.util.TreeMap;

public class PortScannerCSV {
    public static void main(String[] args) {

        String targetHost = "localhost";
        int minPort = 1;
        int maxPort = 65535;


        TreeMap<Integer, String> portData = new TreeMap<>();


        String pathToCsv = "service-names-port-numbers.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(pathToCsv))) {
            String line;
            while ((line = br.readLine()) != null) {

                if (!line.contains(",")) {
                    continue;
                }


                String[] columns = line.split(",");


                if (columns.length >= 4) {
                    try {

                        int portNumber = Integer.parseInt(columns[1].trim());
                        String description = columns[3].trim();


                        if (!description.isEmpty()) {
                            portData.put(portNumber, description);
                        }
                    } catch (NumberFormatException e) {

                    }
                } else {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Keys and Values");
        System.out.println("---------------");


        try (Jedis jedis = new Jedis("localhost", 6379)) {
            for (int port = minPort; port <= maxPort; port++) {
                try {
                    Socket socket = new Socket(targetHost, port);


                    if (portData.containsKey(port)) {
                        String description = portData.get(port);


                        if (!description.isEmpty()) {
                            System.out.println("Port " + port + ": " + description);
                            jedis.set(String.valueOf(port), description);


                            String value = jedis.get(String.valueOf(port));
                        }
                    }
                    socket.close();
                } catch (IOException e) {
                }
            }
        } catch (JedisConnectionException e) {
            System.out.println("Could not connect to Redis: " + e.getMessage());
        }
    }
}