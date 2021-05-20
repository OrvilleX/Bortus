package com.orvillex.bortus.datapump.service;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public interface MqttService {
    /**
     * 创建MQTT客户端
     */
    IMqttAsyncClient createClient(String clientid) throws MqttException;
}
