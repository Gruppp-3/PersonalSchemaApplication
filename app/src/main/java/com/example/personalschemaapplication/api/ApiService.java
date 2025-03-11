package com.example.personalschemaapplication.api;

import com.example.personalschemaapplication.model.EmployeeAvailability;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Om du fortfarande använder denna endpoint:
    @GET("api/v1/employees/id")
    Call<Map<String, Object>> getEmployeeId(@Query("email") String email);

    // Nu ändrad till sträng
    @GET("api/v1/employees/verify/{id}")
    Call<Boolean> verifyEmployeeId(@Path("id") long id);

    @GET("api/v1/employees/{employeeId}/schedule")
    Call<List<Map<String, Object>>> getEmployeeSchedule(@Path("employeeId") long employeeId);

    @GET("api/v1/employees/{employeeId}/availability")
    Call<List<EmployeeAvailability>> getEmployeeAvailability(@Path("employeeId") long employeeId);

}
