package com.GP.mqtt;

import com.GP.labyrinth.LabyrinthModel;

import java.util.List;

public class MQTTManager {

    public void connect(String brokerParam) {
        try {
            ciendId.
        }
    }
    public interface MQTTEventListener {
        void OnSensorChanged(float[] values);
        void OnTime(int minutes, int seconds);
    }

    private List<MQTTEventListener> eventListeners;

    public void subscribe(MQTTEventListener listenerParam) {
        eventListeners.add(listenerParam);
    }

    public void unsubscribe(MQTTEventListener listenerParam) {
        eventListeners.remove(listenerParam);
    }

    private void NotifySensorChanged(float[] values) {

        for (MQTTEventListener listener : eventListeners) {
            listener.OnSensorChanged(values);
        }
    }
}
