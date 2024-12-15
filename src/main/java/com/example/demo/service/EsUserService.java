package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.example.demo.entity.EsUser;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsUserService {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 创建或更新产品
    public Boolean indexExists() throws IOException {
        EsUser esUser = EsUser.builder().id("1").name("testUser").age(30).description("test").build();
        String idx = "es_user";
        boolean exists = restHighLevelClient.indices().exists(new GetIndexRequest(idx), RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest(idx);
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            System.out.println(response.index() + "   创建index成功");
            exists = restHighLevelClient.indices().exists(new GetIndexRequest(idx), RequestOptions.DEFAULT);
            System.out.println(exists + "   创建index返回");

        }

        String id = "1";
        //新增文档
        IndexRequest indexRequest = new IndexRequest(idx);
        indexRequest.id(id);
        indexRequest.source(JSON.toJSONString(esUser), XContentType.JSON);
        indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("状态：" + index.status().getStatus());

        //修改文档
        UpdateRequest updateRequest = new UpdateRequest(idx, id);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "testUser1");
        updateRequest.doc(map);
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println("状态：" + update.status().getStatus());

        //删除文档
        DeleteRequest deleteRequest = new DeleteRequest(idx,id);
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("状态：" + delete.status().getStatus());

        //批量新增文档
        List<EsUser> list = new ArrayList<>();

        EsUser esUserBulk1 = EsUser.builder().id("10").name("testUser10").age(30).description("testUser10").build();
        list.add(esUserBulk1);

        EsUser esUserBulk2 = EsUser.builder().id("11").name("testUser11").age(30).description("testUser11").build();
        list.add(esUserBulk2);

        EsUser esUserBulk3 = EsUser.builder().id("12").name("testUser12").age(30).description("testUser12").build();
        list.add(esUserBulk3);

        //批量导入
        BulkRequest bulk = new BulkRequest(idx);

        for (EsUser doc : list) {
            IndexRequest idxRequest = new IndexRequest();
            idxRequest.id(doc.getId().toString());
            idxRequest.source(JSON.toJSONString(doc), XContentType.JSON);
            bulk.add(idxRequest);
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulk, RequestOptions.DEFAULT);
        System.out.println("状态：" + bulkResponse.status().getStatus());

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(idx);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println("查询结果: " + hit.getSourceAsString());
        }
        restHighLevelClient.close();
        //删除索引
        //DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(idx);
        //AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        //System.out.println(acknowledgedResponse.isAcknowledged() + "   是否已删除");
        return exists;
    }

}