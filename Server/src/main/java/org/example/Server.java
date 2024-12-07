package org.example;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.time.LocalDateTime;

public class Server {

    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";  // Replace with your broker URL
    private static final String TOPIC_SUB_ANDROID_APP = "project/long_lat_deviceID";
    private static final String TOPIC_PUB_ANDROID_APP = "project/server_message";
    private static final String TOPIC_SUB_IOT1 = "project/iot_data1";
    private static final String TOPIC_SUB_IOT2 = "project/iot_data2";
    private static final String CLIENT_ID = MqttClient.generateClientId();
    private static volatile boolean keepRunning = true;
    static double lat1 = 0.0, lon1 = 0.0, lat2a = 0.0, lon2a = 0.0, lat2b = 0.0, lon2b = 0.0, distance = 0.0;
    static boolean eventOD1 = false, eventGas1 = false, eventTemp1 = false, eventUV1 = false, eventGeneral1 = false;
    static boolean eventOD2 = false, eventGas2 = false, eventTemp2 = false, eventUV2 = false, eventGeneral2 = false;
    static double measurement = 0.0;
    static MqttClient client;

    public static void main(String[] args) throws MqttException {


        Thread server = serverStart();

        // Wait for user input to stop the loop
        System.out.println("Press Enter to stop the server...");
        try {
            int input;
            // I put it here to make the warning vanish, but the idea is that server will end when user gives an input
            while ((input = System.in.read()) != '\n') {
                System.out.print((char) input);
            }
        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
        }


        // Set the flag to false to stop the loop
        keepRunning = false;
        System.out.println("Signal received to stop the loop.");


        try {
            server.join();
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }
        client.disconnect();
    }

    private static Thread serverStart() {
        Thread server = new Thread(() -> {
            // Perform MQTT operations
            client = null;
            try {
                client = getMqttClient();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Connected to broker: " + BROKER_URL);

            // Subscribe to the topic
            try {
                client.subscribe(TOPIC_SUB_ANDROID_APP);
                client.subscribe(TOPIC_SUB_IOT1);
                client.subscribe(TOPIC_SUB_IOT2);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }

            while (keepRunning) {
                Thread.onSpinWait();
            }
            System.out.println("Loop terminated.");
        });

        // Start the server
        server.start();
        return server;
    }

    private static MqttClient getMqttClient() throws MqttException {
        MqttClient client = new MqttClient(BROKER_URL, CLIENT_ID);

        // Set callback to handle messages and connection loss
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + cause.getMessage() + cause.getCause().toString());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws MqttException {
                switch (topic) {
                    case TOPIC_SUB_ANDROID_APP -> {
                        String payload = new String(message.getPayload());
                        String[] data = payload.split(" ");
                        String deviceID = data[0].trim();
                        lat1 = Double.parseDouble(data[1].trim());
                        lon1 = Double.parseDouble(data[2].trim());
                        System.out.println("Received data: " + deviceID + " " + lat1 + " " + lon1);
                    }
                    case TOPIC_SUB_IOT1 -> {
                        String payload = new String(message.getPayload());
                        System.out.println("Received data: " + payload);
                        String[] data = payload.split(" ");

                        // Ensure data has at least 4 elements (lat, lon, and at least one sensor/measurement pair)
                        if (data.length < 4 || data.length % 2 != 0) {
                            System.out.println("Invalid data format received.");
                            return;
                        }
                        lat2a = Double.parseDouble(data[0].trim());
                        lon2a = Double.parseDouble(data[1].trim());

                        int columns = 2;
                        int rows = (data.length - 2) / columns;  // Adjust rows for sensor data only
                        String[][] reshapedData = new String[rows][columns];

                        // Populate the 2D array with sensor/measurement data starting from index 2
                        for (int i = 2; i < data.length; i++) {
                            reshapedData[(i - 2) / columns][(i - 2) % columns] = data[i];
                        }

                        // Process each sensor/measurement pair
                        for (int i = 0; i < rows; i++) {
                            String sensor = reshapedData[i][0];
                            measurement = Double.parseDouble(reshapedData[i][1]);
                            switch (sensor) {
                                case "OD" -> {
                                    if (measurement < 0 || measurement > 0.25) {
                                        System.out.println("Invalid data for optical density");
                                        return;
                                    }
                                    if (measurement > 0.14) {
                                        System.out.println("EVENT for optical density");
                                        eventOD1 = true;
                                        eventGeneral1 = true;
                                    }
                                }
                                case "GAS" -> {
                                    if (measurement < 0 || measurement > 11) {
                                        System.out.println("Invalid data for gas content");
                                        return;
                                    }
                                    if (measurement > 9.15) {
                                        System.out.println("EVENT for gas content");
                                        eventGas1 = true;
                                        eventGeneral1 = true;
                                    }
                                }
                                case "TEMP" -> {
                                    if (measurement < -5 || measurement > 80) {
                                        System.out.println("Invalid data for temperature");
                                        return;
                                    }
                                    if (measurement > 50) {
                                        System.out.println("EVENT for temperature");
                                        eventTemp1 = true;
                                        eventGeneral1 = true;
                                    }
                                }
                                case "UV" -> {
                                    if (measurement < 0 || measurement > 11) {
                                        System.out.println("Invalid data for UV");
                                        return;
                                    }
                                    if (measurement > 6) {
                                        System.out.println("EVENT for UV");
                                        eventUV1 = true;
                                        eventGeneral1 = true;
                                    }
                                }
                            }
                        }


                    }
                    case TOPIC_SUB_IOT2 -> {
                        String payload = new String(message.getPayload());
                        String[] data = payload.split(" ");
                        lat2b = Double.parseDouble(data[0].trim());
                        lon2b = Double.parseDouble(data[1].trim());


                        int n = data.length;
                        int columns = 2;
                        int rows = n / columns;
                        String[][] reshapedData = new String[rows][columns];
                        // Populate the 2D array
                        for (int i = 2; i < n; i++) {
                            reshapedData[i / columns][i % columns] = data[i];
                        }

                        for(int i = 2; i < n; i++ ) {
                            String sensor = reshapedData[i][0];
                            measurement = Double.parseDouble(reshapedData[i][1]);
                            switch (sensor) {
                                case "OD" -> {
                                    if (measurement < 0 || measurement > 0.25) {
                                        System.out.println("Invalid data for optical density");
                                        return;
                                    }
                                    if (measurement > 0.14) {
                                        System.out.println("EVENT for optical density");
                                        eventOD2 = true;
                                        eventGeneral2 = true;
                                    }
                                }
                                case "GAS" -> {
                                    if (measurement < 0 || measurement > 11) {
                                        System.out.println("Invalid data for gas content");
                                        return;
                                    }
                                    if (measurement > 9.15) {
                                        System.out.println("EVENT for gas content");
                                        eventGas2 = true;
                                        eventGeneral2 = true;
                                    }
                                }
                                case "TEMP" -> {
                                    if (measurement < -5 || measurement > 80) {
                                        System.out.println("Invalid data for temperature");
                                        return;
                                    }
                                    if (measurement > 50) {
                                        System.out.println("EVENT for temperature");
                                        eventTemp2 = true;
                                        eventGeneral2 = true;
                                    }
                                }
                                case "UV" -> {
                                    if (measurement < 0 || measurement > 11) {
                                        System.out.println("Invalid data for UV");
                                        return;
                                    }
                                    if (measurement > 6) {
                                        System.out.println("EVENT for UV");
                                        eventUV2 = true;
                                        eventGeneral2 = true;
                                    }
                                }
                            }
                        }
                    }
                }

                LocalDateTime currentDateTime = LocalDateTime.now();


                // If both sensors trigger an event
                if(eventGeneral1 && eventGeneral2) {
                    double[] midPoint = (new Distance()).findMidpoint(lat2a, lon2a, lat2b, lon2b);
                    distance = (new Distance()).distance(lat1, lon1, midPoint[0], midPoint[1], "K");
                } else if(eventGeneral1) { // Only first hit
                    distance = (new Distance()).distance(lat1, lon1, lat2a, lon2a, "K");
                } else { // Only second hit
                    distance = (new Distance()).distance(lat1, lon1, lat2b, lon2b, "K");
                }

                String riskLevel;
                if(eventGeneral1) {
                    if (eventOD1 && eventGas1 && !eventTemp1 && !eventUV1) {
                        riskLevel = "High";
                        DatabaseConnection.insertEvent(currentDateTime.toString(), lat2a, lon2a, measurement, 2);
                        // Publish a message to the topic
                        String messageContent = riskLevel + " " + distance;
                        MqttMessage messageToAndroid = new MqttMessage(messageContent.getBytes());
                        messageToAndroid.setQos(2);
                        client.publish(TOPIC_PUB_ANDROID_APP, messageToAndroid);
                    } else if (!eventOD1 && !eventGas1 && eventTemp1 && eventUV1) {
                        riskLevel = "Moderate";
                        DatabaseConnection.insertEvent(currentDateTime.toString(), lat2a, lon2a, measurement, 1);
                        // Publish a message to the topic
                        String messageContent = riskLevel + " " + distance;
                        MqttMessage messageToAndroid = new MqttMessage(messageContent.getBytes());
                        messageToAndroid.setQos(2);
                        client.publish(TOPIC_PUB_ANDROID_APP, messageToAndroid);
                    } else if (eventOD1 && eventGas1 && eventTemp1 && eventUV1) {
                        riskLevel = "High";
                        DatabaseConnection.insertEvent(currentDateTime.toString(), lat2a, lon2a, measurement, 2);
                        // Publish a message to the topic
                        String messageContent = riskLevel + " " + distance;
                        MqttMessage messageToAndroid = new MqttMessage(messageContent.getBytes());
                        messageToAndroid.setQos(2);
                        client.publish(TOPIC_PUB_ANDROID_APP, messageToAndroid);
                    }

                } else if(eventGeneral2) {
                    if (eventOD2 && eventGas2 && !eventTemp2 && !eventUV2) {
                        riskLevel = "High";
                        DatabaseConnection.insertEvent(currentDateTime.toString(), lat2b, lon2b, measurement, 2);
                        // Publish a message to the topic
                        String messageContent = riskLevel + " " + distance;
                        MqttMessage messageToAndroid = new MqttMessage(messageContent.getBytes());
                        messageToAndroid.setQos(2);
                        client.publish(TOPIC_PUB_ANDROID_APP, messageToAndroid);
                    } else if (!eventOD2 && !eventGas2 && eventTemp2 && eventUV2) {
                        riskLevel = "Moderate";
                        DatabaseConnection.insertEvent(currentDateTime.toString(), lat2b, lon2b, measurement, 1);
                        // Publish a message to the topic
                        String messageContent = riskLevel + " " + distance;
                        MqttMessage messageToAndroid = new MqttMessage(messageContent.getBytes());
                        messageToAndroid.setQos(2);
                        client.publish(TOPIC_PUB_ANDROID_APP, messageToAndroid);
                    } else if (eventOD2 && eventGas2 && eventTemp2 && eventUV2) {
                        riskLevel = "High";
                        DatabaseConnection.insertEvent(currentDateTime.toString(), lat2b, lon2b, measurement, 2);
                        // Publish a message to the topic
                        String messageContent = riskLevel + " " + distance;
                        MqttMessage messageToAndroid = new MqttMessage(messageContent.getBytes());
                        messageToAndroid.setQos(2);
                        client.publish(TOPIC_PUB_ANDROID_APP, messageToAndroid);
                    }
                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Message delivery complete");
            }
        });

        // Configure connection options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        // Connect to the broker
        client.connect(options);
        return client;
    }


}
