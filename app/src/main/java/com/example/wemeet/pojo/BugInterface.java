package com.example.wemeet.pojo;

import com.example.wemeet.util.ReturnVO;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BugInterface {
    @GET("getAroundBugs")
    Call<List<Bug>> getAroundBugs(@Query("userLon") double userLon, @Query("userLat") double userLat);

    @POST("addBug")
    Call<ReturnVO> addBug(@Body Bug bug);

    @GET("addUserCatchesBugConstraint")
    Call<ReturnVO> addUserCatchesBugConstraint(
            @Query("bugId") Long bugId,
            @Query("email") String email,
            @Query("userAnswer") String userAnswer
    );

    @GET("getCatchRecordsByEmail")
    Call<Set<CatcherBugRecord>> getCatchRecordsByEmail(@Query("email") String email);

    @PUT("bug/{id}")
    Call<ReturnVO> updateBug(@Path("id") Long id, @Body Bug bug);
}
