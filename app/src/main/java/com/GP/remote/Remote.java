package com.GP.remote;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import com.GP.mqtt.MQTTManager;

public class Remote implements MQTTManager.MQTTEventListener {
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
     * Constructor
     * @param context application context
     */
    private Remote(Context context) {
        // Prevent from the reflection api.
        if (uniqueInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
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
            uniqueInstance.mqttConnection = new MQTTManager(getInstance(), "test");
            listeners = new ArrayList<>();

        } else {
            Log.d("Remote", "Instance already initialized");
        }
    }

    public void connectToBroker(String brokerParam) {
        this.mqttConnection.connect(brokerParam);
        this.mqttConnection.subscribe("mpu/I12");
        this.mqttConnection.subscribe("temp/I12");
        this.mqttConnection.subscribe("time/I12");
    }

    public void sendGameWonMessage() {
        Log.d("Remote", "Sending game won message to broker");
        this.mqttConnection.publish("Crazy/I12", "Game won");
    }

    public void sendGameStartMessage() {
        Log.d("Remote", "Sending game won message to broker");
        this.mqttConnection.publish("Crazy/I12", "Game start");
    }

    @Override
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