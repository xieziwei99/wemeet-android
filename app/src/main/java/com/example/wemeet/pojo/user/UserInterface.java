package com.example.wemeet.pojo.user;

import com.example.wemeet.pojo.Bug;
import com.example.wemeet.util.ReturnVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserInterface {
    @GET("user")
    Call<User> getUserByEmail(@Query("email") String email);

    @POST("user/login")
    Call<ReturnVO> login(@Body User user);

    @PUT("user")
    Call<ReturnVO> updateUser(@Body User user);

    @PUT("user/email/{email}/score/{score}")
    Call<ReturnVO> changeScoreOfUser(@Path("email") String email, @Path("score") double score);

    @GET("user/plantBugs")
    Call<List<Bug>> getPlantBugsByUserEmail(@Query("email") String email);
}
