package com.orvillex.bortus.datapump.executor.elasticsearc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.orvillex.bortus.job.log.JobLogger;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ESClientHelper {
    public static Client createClient(AbstractParam param) {
        List<String> urls = param.getConnections();
        TransportClient transportClient = null;
        Settings settings = Settings.builder().put("client.transport.ignore_cluster_name", true).build();
        try {
            JobLogger.log("准备建立连接");
            transportClient = new PreBuiltTransportClient(settings);

            for (String hostAndPort : urls) {
                String host = hostAndPort.split(":")[0];
                String port = hostAndPort.split(":")[1];
                JobLogger.log("填入连接配置");
                transportClient.addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(host), Integer.parseInt(port)));
                JobLogger.log("连接配置填入成功");
            }
        } catch (UnknownHostException e) {
            JobLogger.log(e);
        }
        return transportClient;
    }
}
