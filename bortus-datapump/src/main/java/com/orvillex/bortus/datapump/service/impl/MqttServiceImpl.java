package com.orvillex.bortus.datapump.service.impl;

import com.orvillex.bortus.datapump.service.MqttService;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.stereotype.Service;

@Service
public class MqttServiceImpl implements MqttService {
    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String url;

    private MqttConnectOptions mqttConnectOptions;
    private DefaultMqttPahoClientFactory factory;

    @Override
    public IMqttAsyncClient createClient(String clientid) throws MqttException {
        if (factory == null) {
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setUserName(username);
            mqttConnectOptions.setPassword(password.toCharArray());
            mqttConnectOptions.setConnectionTimeout(10);
            mqttConnectOptions.setKeepAliveInterval(3000);
            mqttConnectOptions.setAutomaticReconnect(true);
    
            factory = new DefaultMqttPahoClientFactory();
            factory.setConnectionOptions(mqttConnectOptions);
        }
        IMqttAsyncClient client = factory.getAsyncClientInstance(url, clientid);
        client.connect(mqttConnectOptions).waitForCompletion();
        
        return client;
    }
}
