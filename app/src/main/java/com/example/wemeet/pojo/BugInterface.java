package com.example.wemeet.pojo;

import com.example.wemeet.util.ReturnVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BugInterface {
    @GET("getAroundBugs")
    Call<List<Bug>> getAroundBugs(@Query("userLon") double userLon, @Query("userLat") double userLat);

    @POST("addBug")
    Call<ReturnVO> addBug(@Body Bug bug);
}
