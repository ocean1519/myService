package com.example.demo.utils;

import com.alibaba.fastjson2.JSON;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class OKHttp {

    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectionPool(new okhttp3.ConnectionPool(50, 5, TimeUnit.MINUTES))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                //.retryOnConnectionFailure(true)
                .build();
    }

    public static String postJson(String url, String body, Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), body);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .headers(Headers.of(headers))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("请求异常!url=" + url + " param=" + body +" message=" + response);
            String result = response.body().string();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String postJsonAsync(String url, String body, Callback callback) {
        return postJsonAsync(url, body, null, callback);
    }

    public static String postJsonAsync(String url, String body, Map<String, String> headers, Callback callback) {

        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), body);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .headers(Headers.of(headers == null ? new HashMap<>() : headers))
                .build();
        client.newCall(request).enqueue(callback);
        return "请求成功";
    }

    public static String postJson(String url, String body) {
        return postJson(url, body, null);
    }

    public static String get(String url, Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(Headers.of(headers))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("请求异常!url=" + url + " message=" + response);
            String result = response.body().string();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String get(String url) {
        return get(url, null);
    }

    public static String getAsync(String url, Map<String, String> headers, Callback callback) {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(Headers.of(headers == null ? new HashMap<>() : headers))
                .build();
        client.newCall(request).enqueue(callback);
        return "请求成功";
    }

    public static String getAsync(String url, Callback callback) {
        return getAsync(url, null, callback);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Long timestamp = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            String body = "{\"pageNum\":1,\"pageSize\":8,\"params\":{\"contentTypes\":[11,12,16],\"firstColumnId\":81}}";
            System.out.println(OKHttp.postJsonAsync("https://liveapi.cn/djb/yb-strategy-api-ntk/contentManager/getContentsList", body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("返回结果:" + response.body().string());
                }
            }));
        }
        System.out.println(System.currentTimeMillis() - timestamp);
    }
}
