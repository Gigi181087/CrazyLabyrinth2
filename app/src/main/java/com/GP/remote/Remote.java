package com.GP.remote;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import com.GP.mqtt.MQTTManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Remote implements MQTTManager.MQTTEventListener {

    private static Remote instance;
    private MqttAndroidClient mqttAndroidClient;
    private String clientId = "your_client_id";
    private Context context;

    private String serverUri;

    private Remote(Context context) {
        if (uniqueInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.context = context.getApplicationContext();
    }

    public static synchronized Remote getInstance(Context context) {
        if (instance == null) {
            instance = new Remote(context);
        }
        return instance;
    }

    public void connect(String brokerIp) {
        serverUri = "tcp://" + brokerIp + ":1883";
        MqttConnectOptions options = new MqttConnectOptions();

        try {
            mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Verarbeitung von Verbindungsverlusten
                    if (cause != null) {
                        cause.printStackTrace();
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    onMessage(topic, message.getPayload().toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                        String messageId = String.valueOf(token.getMessageId());
                        Log.d("Remote", "Nachricht zugestellt - ID: " + messageId);
                        // Weitere Verarbeitung der zugestellten Nachricht...

                }
            });

            IMqttToken token = mqttAndroidClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Verarbeitung bei Verbindungsfehler
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void subscribeToTopic(String topic) {
        try {
            IMqttToken token = mqttAndroidClient.subscribe(topic, 0);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Verarbeitung bei erfolgreichem Abonnement
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Verarbeitung bei Abonnementfehler
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttAndroidClient.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static Remote uniqueInstance;

    private static List<ListenerRemoteEvents> listeners;
    static State state;
    static MQTTManager mqttConnection;


    /**
     * Event interface for remote
     */
    public interface ListenerRemoteEvents {
        void onTimeMessage(int millisecondsParam);
        void onTempMessage(float tempParam);
        void onSensorMessage(float[] forceParams);
    }

    /**
     * Enum which holds the state of a remote connection
     */
    public enum State {
        DISCONNECTED,
        CONNECTED
    }


    /**
     * Gives the unique instance of the remote
     * @return unique instance
     */
    public static Remote getInstance() {
        Log.d("Remote", "Get instance called");

        if(uniqueInstance == null) {
            Log.d("Remote", "Instance not initialized");
            throw new NullPointerException("Call initialize() before getting the instance!");
        }

        return uniqueInstance;
    }

    /**
     * Initializes the remote, only one instance allowed
     * @param applicationContext application context
     */
    public synchronized static void initialize(Context applicationContext) {
        Log.d("Remote", "Initialized called");

        if (applicationContext == null) {
            Log.d("Remote", "Application Context can't be null");
            throw new NullPointerException("Provided application context is null");

        } else if (uniqueInstance == null) {
            uniqueInstance = new Remote(applicationContext);
            listeners = new ArrayList<>();

        } else {
            Log.d("Remote", "Instance already initialized");
        }
    }

    public void connectToBroker(String brokerParam) {
        connect(brokerParam);
        subscribeToTopic("mpu/I12");
        subscribeToTopic("temp/I12");
        subscribeToTopic("time/I12");
    }

    public void sendGameWonMessage() {
        Log.d("Remote", "Sending game won message to broker");
        uniqueInstance.publishMessage("Crazy/I12", "Game won");
    }

    public void sendGameStartMessage() {
        Log.d("Remote", "Sending game won message to broker");
        uniqueInstance.publishMessage("Crazy/I12", "Game start");
    }

    public void onMessage(String topicParam, String messageParam) {

        try {

            if(topicParam.equals("mpu/I12")) {
                float[] values = new float[6];
                String[] parts = messageParam.split("\\s+");

                for (String part : parts) {
                    String[] subParts = part.split(":\\s+");
                    String letter = subParts[0];
                    float value = Float.parseFloat(subParts[1]);

                }
                notifySensorMessage(values);

            } else if(topicParam.equals("time/I12" )){
                int _timeValue = Integer.parseInt(messageParam);
                this.notifyTimeMessage(_timeValue);

            } else if(topicParam.equals("temp/I12")) {
                float _tempValue = Float.parseFloat(messageParam);
                notifyTempMessage(_tempValue);
            }
        } catch (NumberFormatException ex) {
            Log.d("Remote", "Exception on " + messageParam + "-message: " + ex.getMessage());
        }
    }

    @Override
    public void onConnectionSuccess() {

    }

    @Override
    public void onConnectionFailure() {

    }

    @Override
    public void onDisconnectionSuccess() {

    }

    @Override
    public void onDisconnectionFailure() {

    }

    /**
     *Notifies all registered listeners about a time message.
     *@param timeParam The time parameter to be passed to the listeners.
     */
     void notifyTimeMessage(int timeParam) {

        for(ListenerRemoteEvents listener : listeners) {
            listener.onTimeMessage(timeParam);
        }
    }

    /**
     *Notifies all registered listeners about a temperature message.
     *@param tempParam The temperature parameter to be passed to the listeners.
     */
    void notifyTempMessage(float tempParam) {

        for(ListenerRemoteEvents listener : listeners) {
            listener.onTempMessage(tempParam);
        }
    }

    /**
     *Notifies all registered listeners about a sensor message containing force parameters.
     *@param forceParams An array of force parameters to be passed to the listeners.
     */
    void notifySensorMessage(float[] forceParams) {

        for(ListenerRemoteEvents listener : listeners) {
            listener.onSensorMessage(forceParams);
        }
    }

    public void registerListener(ListenerRemoteEvents listenerParam) {
        if(uniqueInstance != null) {
            listeners.add(listenerParam);
        } else {
            Log.d("Remote", "Remote not initialized");
        }
    }
}