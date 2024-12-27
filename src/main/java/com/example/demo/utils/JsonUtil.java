package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将Java对象转换为JSON字符串
     * @param obj 需要转换的Java对象
     * @return JSON格式的字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的Java对象
     * @param jsonStr JSON格式的字符串
     * @param clazz 目标对象的Class类型
     * @param <T> 泛型类型
     * @return 转换后的Java对象实例
     */
    public static <T> T toObject(String jsonStr, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON string to object", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的Java List对象
     * @param jsonStr JSON格式的字符串
     * @param elementType 列表中元素的Class类型
     * @param <T> 泛型类型
     * @return 转换后的Java List对象实例
     */
    public static <T> List<T> jsonToList(String jsonStr, Class<T> elementType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, elementType);
            return objectMapper.readValue(jsonStr, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON string to list", e);
        }
    }
}
