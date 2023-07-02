package com.GP.mqtt;

import android.content.Context;
import android.util.Log;

//MQTT imports
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;

// https://apgapg.medium.com/why-you-should-use-singleton-pattern-in-your-android-application-b02b31111086
public class MQTTManager {
    private static MQTTManager uniqueInstance;
    private static String clientId;

    private static final String mpu_sub_topic = "mpu/X00";      // Anpassen
    private static final String temp_sub_topic = "temp/X00";    // Anpassen
    private static final String pub_topic = "finished/X00";     // Anpassen
    private int qos = 0; // MQTT quality of service
    private String data;
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttClient client;
    private String TAG = "MQTTManager";

    private MQTTManager(Context context) {
        // Prevent from the reflection api.
        if (uniqueInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }
    public static MQTTManager getInstance() {

        if(uniqueInstance == null) {
            throw new NullPointerException("Call initialize() before getting the instance!");
        }

        return uniqueInstance;
    }

    public synchronized static void initialize(Context applicationContext) {
        if (applicationContext == null)
            throw new NullPointerException("Provided application context is null");
        else if (uniqueInstance == null) {
            uniqueInstance = new MQTTManager(applicationContext);
        }
    }

    // die IP-Adresse bitte in SharedPreferences und über Menü änderbar
    public String Broker = "tcp://192.168.1.13:1883";

    /**
     * Connect to broker and
     * @param brokerParam Broker to connect to
     */
    public void connect(String brokerParam) {
        try {
            clientId = MqttClient.generateClientId();
            client = new MqttClient(brokerParam, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            Log.d(TAG, "Connecting to broker: " + brokerParam);
            client.connect(connOpts);
            Log.d(TAG, "Connected with broker: " + brokerParam);
        } catch (MqttException me) {
            Log.e(TAG, "Reason: " + me.getReasonCode());
            Log.e(TAG, "Message: " + me.getMessage());
            Log.e(TAG, "localizedMsg: " + me.getLocalizedMessage());
            Log.e(TAG, "cause: " + me.getCause());
            Log.e(TAG, "exception: " + me);
        }
    }

    /**
     * Publishes a message via MQTT (with fixed topic)
     * @param topic topic to publish with
     * @param msg message to publish with publish topic
     */
    private void publish(String topic, String msg) {
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
     * @param topic Topic to subscribe to
     */
    private void subscribe(String topic) {
        try {
            client.subscribe(topic, qos, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage msg) throws Exception {
                    String message = new String(msg.getPayload());
                    Log.d(TAG, "Message with topic " + topic + " arrived: " + message);


                    NotifyNewMessage(topic, msg.getPayload().toString());
                }
            });
            Log.d(TAG, "subscribed to topic " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public interface MQTTEventListener {
        void onNewMessage(String topicParam, String messageParam);
    }

    private List<MQTTEventListener> eventListeners;

    public void registerListener(MQTTEventListener listenerParam) {
        eventListeners.add(listenerParam);
    }

    public void removeListener(MQTTEventListener listenerParam) {
        eventListeners.remove(listenerParam);
    }

    private void NotifyNewMessage(String topicParam, String messageParam) {

        for (MQTTEventListener listener : eventListeners) {
            listener.onNewMessage(topicParam, messageParam);
        }
    }
}
