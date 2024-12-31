package com.example.demo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.demo.config.ElasticsearchConfig;
import com.example.demo.entity.EsUser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EsUserService {

    @Autowired
    private ElasticsearchClient client;

    // 创建或更新产品
    public Boolean testEs() throws IOException {

        createIndex();

        saveDocument();

        updateDocument();

        deleteDocument();

        queryDocument();

        queryHighDocument();
        return false;
    }

    private void createIndex() throws IOException {
        ExistsRequest indexReq = new ExistsRequest.Builder().index("es_user1").build();
        BooleanResponse exist = client.indices().exists(indexReq);
        if (!exist.value()) {
            CreateIndexRequest indexRequest = new CreateIndexRequest.Builder().index("es_user1").build();
            CreateIndexResponse response = client.indices().create(indexRequest);
            if (response.acknowledged()) {
                System.out.println("Index create successfully.");
            }
        }

    }

    private void queryHighDocument() {

    }

    private void queryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("es_user")
                .query(q -> q
                        .match(m -> m
                                .field("name")
                                .query("testUser")
                                .operator(Operator.And)
                        )
                ).build();
        SearchResponse<EsUser> response = client.search(searchRequest, EsUser.class);
        long totalHits = response.hits().total().value();
        System.out.println("Total hits: " + totalHits);
        for (Hit<EsUser> hit : response.hits().hits()) {
            EsUser user = hit.source();
            System.out.println("Found user: " + user.getName());
        }
        System.out.println();
    }

    private void deleteDocument() throws IOException {

        co.elastic.clients.elasticsearch.core.ExistsRequest indexReq = new co.elastic.clients.elasticsearch.core.ExistsRequest.Builder().index("es_user1").id("2").build();
        if (client.exists(indexReq).value()) {
            System.out.println("Document exist");
        }

        DeleteRequest request = new DeleteRequest.Builder().index("es_user").id("2").build();

        DeleteResponse response = client.delete(request);
        // 检查响应结果
        if (Result.Deleted == response.result()) {
            System.out.println("Document delete successfully.");
        }
    }

    private void updateDocument() throws IOException {
        EsUser esUser = new EsUser();
        esUser.setAge(41);
        UpdateRequest request = new UpdateRequest.Builder().index("es_user").id("2").doc(esUser).build();
        UpdateResponse<EsUser> response = client.update(request, EsUser.class);
        // 检查响应结果
        if (Result.Updated == response.result()) {
            System.out.println("Document update successfully.");
        }
    }

    public void saveDocument() throws IOException {
        // 创建一个新的用户对象
        EsUser user = new EsUser("2", "testUser", "test", 30);

        // 创建索引请求并执行
        IndexRequest<EsUser> request = new IndexRequest.Builder<EsUser>()
                .index("es_user")
                .id(user.getId())
                .document(user)
                .build();

        IndexResponse response = client.index(request);

        // 检查响应结果
        if (Result.Created == response.result()) {
            System.out.println("Document indexed successfully.");
        }
    }
}