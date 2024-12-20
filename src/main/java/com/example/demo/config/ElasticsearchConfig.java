package com.example.demo.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    final String hostname = "127.0.0.1";
    final int port = 9200;
    final String username = "elastic";
    final String password = "mypassword";

    @Bean
    public ElasticsearchClient restHighLevelClient() {
        // 设置认证信息
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        // 创建低级别的 RestClient 并设置认证
        RestClient restClient = RestClient.builder(
                        new HttpHost(hostname, port, "http"))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();

        // 使用 RestClient 创建传输层
        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // 创建高级别的 Elasticsearch 客户端
        return new ElasticsearchClient(transport);
    }
}