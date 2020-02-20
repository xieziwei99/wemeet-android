//package com.example.wemeet.util;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.io.IOException;
//
//public class JacksonUtil {
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    // 不提供构造函数
//    private JacksonUtil() { }
//
////    public static ObjectMapper getInstance() {
////        return objectMapper;
////    }
//
//    /**
//     * 将对象转换成json串，也可以将list和array转换成json串
//     * @param o 对象，list or array
//     * @return json串
//     * @throws JsonProcessingException 抛出由上层处理
//     */
//    public static String otoj(Object o) throws JsonProcessingException {
//        return objectMapper.writeValueAsString(o);
//    }
//
//    /**
//     * 将 json 串转换成 pojo
//     * @param jsonStr json 串
//     * @param clazz 类的类型
//     * @param <T> 泛型
//     * @return pojo
//     * @throws IOException 抛出由上层处理
//     */
//    public static <T> T jtoo(String jsonStr, Class<T> clazz) throws IOException {
//        return objectMapper.readValue(jsonStr, clazz);
//    }
//}
