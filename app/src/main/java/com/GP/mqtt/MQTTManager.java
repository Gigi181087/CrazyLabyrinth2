package com.GP.mqtt;

import android.content.Context;
import android.util.Log;

//MQTT imports
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.EventListener;
import java.util.List;

// https://apgapg.medium.com/why-you-should-use-singleton-pattern-in-your-android-application-b02b31111086
public class MQTTManager {
    private  String clientId;

    private static final String mpu_sub_topic = "mpu/X00";      // Anpassen
    private static final String temp_sub_topic = "temp/X00";    // Anpassen
    private static final String pub_topic = "finished/X00";     // Anpassen
    private int qos = 0; // MQTT quality of service
    private String data;

    Context context;
    private MqttAndroidClient client;
    private MemoryPersistence persistence = new MemoryPersistence();

    private String TAG = "MQTTManager";
    private MQTTEventListener eventListener;

    public MQTTManager(Context contextParam) {
        String clientId = MqttClient.generateClientId();
        this.context = contextParam;

        eventListener = (MQTTEventListener)contextParam;
        // Prevent from the reflection api.
    }

    public interface MQTTListener {
        public void onConnectionSuccess();
    }

    // die IP-Adresse bitte in SharedPreferences und über Menü änderbar
    public String Broker = "tcp://192.168.1.4:1883";

    /**
     * constructor
     */
    public MQTTManager(Context context, String ip) {
        client = new MqttAndroidClient(context, "tcp://" + ip + ":1883",clientId);
        //client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.43.41:1883",clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    eventListener.onConnectionSuccess();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    eventListener.onConnectionFailure();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }




    /**
     * Publishes a message via MQTT (with fixed topic)
     * @param topic topic to publish with
     * @param msg message to publish with publish topic
     */
    public void publish(String topic, String msg) {
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribes to a given topic
     * @param topicParam Topic to subscribe to
     */
    public void subscribe(String topicParam) {
        try {
            client.subscribe(topicParam, qos);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public interface MQTTEventListener {
        void onMessage(String topicParam, String messageParam);
        void onConnectionSuccess();
        void onConnectionFailure();
        void onDisconnectionSuccess();
        void onDisconnectionFailure();
    }



    private void NotifyNewMessage(String topicParam, String messageParam) {
            eventListener.onMessage(topicParam, messageParam);
    }

    public void published(String topic, String message){

        try {
            client.publish(topic, message.getBytes(),0,false);
            Log.d("Remote", "Message published!");
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect(String brokerAddressParam){

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    eventListener.onConnectionSuccess();
                    Log.d("Remote", "Connected to Broker!");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    eventListener.onConnectionFailure();
                    Log.d("Remote", "Could not connect to Broker!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void disconnect(){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    eventListener.onDisconnectionSuccess();
                    Log.d("Remote", "Disconnected from Broker!");


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    eventListener.onDisconnectionFailure();
                    Log.d("Remote", "Could not disconnect from Broker!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
