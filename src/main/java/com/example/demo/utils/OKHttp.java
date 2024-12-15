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
        for (int i = 0; i < 10; i++) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", String.valueOf(13234210000L + i));
            paramMap.put("postTime", String.valueOf(System.currentTimeMillis()));
            paramMap.put("sign", createSign(paramMap, "YB-0TW37UG9"));

            Map<String, String> header = new HashMap<>();
            header.put("remote-host", "changqing.100live.cn");
            paramMap.put("businessId",  "3");
            //System.out.println(OKHttp.postJson("https://liveapi.cn/djb/yb-user-api-ntk/user/third/login", JSON.toJSONString(paramMap), header));
            System.out.println(OKHttp.getAsync("https://liveapi.cn/djb/yb-cms-api-ntk/titles/getTitles/1/1", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("返回结果:" + response.body().string());
                }
            }));
            //System.out.println("手机号:" + 13234210000L + i);
        }
    }

    public static String createSign(Map<String, String> params, String securitKey) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        // 将参数以参数名的字典升序排序
        Map<String, String> sortParams = new TreeMap<>(params);
        // 遍历排序的字典,并拼接"key=value"格式
        for (Map.Entry<String, String> entry : sortParams.entrySet()) {
            String key = entry.getKey();
            String value =  entry.getValue().trim();
            if (!StringUtils.isEmpty(value))
                sb.append("&").append(key).append("=").append(value);
        }
        String urlAppend = sb.toString().replaceFirst("&","");
        String stringSignTemp = urlAppend + "&" + "key=" + securitKey;
        //将签名使用MD5加密并全部字母变为大写
        String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
        System.out.println(signValue);
        return signValue;
    }
}
