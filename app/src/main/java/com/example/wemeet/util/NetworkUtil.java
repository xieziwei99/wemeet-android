package com.example.wemeet.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class NetworkUtil {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            String baseUrl = "http://101.37.172.100:8080/";
            // 这行可以解决报错：Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path $
//            Gson gson = new GsonBuilder().setLenient().create();
//            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create(gson)).build();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create(mapper)).build();
        }
        return retrofit;
    }
}
