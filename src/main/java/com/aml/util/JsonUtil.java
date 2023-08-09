package com.aml.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJsonString(Object o) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Error while convert Object to Json: {}", "", e);
            return "";
        }
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Error while convert json string to Object: {}", "", e);
            return null;
        }
    }

    public static <T>List<T> parseArray(String json, Class<T> clazz) {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);

        try {
            return mapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("Error while convert json string to array: {}", "", e);
            return new ArrayList<>();
        }
    }
}
