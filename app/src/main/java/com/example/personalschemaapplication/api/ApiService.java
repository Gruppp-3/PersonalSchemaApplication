package com.example.personalschemaapplication.api;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface ApiService {


    // glömt ID
    @GET("api/v1/employees/id")
    Call<Map<String, Object>> getEmployeeId(@Query("email") String email);


    @GET("api/employees/verify/{id}")
    Call<Boolean> verifyEmployeeId(@Path("id") long id);

}
