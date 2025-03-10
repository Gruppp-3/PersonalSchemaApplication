package com.example.personalschemaapplication.api;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Hämta alla skift för ett visst datum
    @GET("api/v1/shifts/date/{date}")
    Call<List<Map<String, Object>>> getShiftsByDate(@Path("date") String date);

    // Hämta skiften för en specifik anställd baserat på deras ID
    @GET("api/v1/shifts/employee/{employeeId}")
    Call<List<Map<String, Object>>> getEmployeeShifts(@Path("employeeId") String employeeId);
    // Verifiera om en anställd finns med ett givet ID
    @GET("api/v1/employees/verify")
    Call<Boolean> verifyEmployeeId(@Query("id") String employeeId);


    // Skapa ett nytt skift (till exempel att en anställd anger sin tillgänglighet)
    @POST("api/v1/shifts")
    Call<Map<String, Object>> createShift(@Body Map<String, Object> shift);

    // Uppdatera ett befintligt skift
    @PUT("api/v1/shifts/{id}")
    Call<Map<String, Object>> updateShift(@Path("id") Long id, @Body Map<String, Object> shift);

    // Ta bort ett skift
    @DELETE("api/v1/shifts/{id}")
    Call<Void> deleteShift(@Path("id") Long id);

    // (Valfritt) Validera att ett skift kan läggas in (t.ex. kontrollera att tid och datum är tillgängligt)
    @GET("api/v1/shifts/validate")
    Call<Boolean> validateShift(@Query("date") String date, @Query("time") String time);

    // glömt ID
    @GET("api/v1/employees/id")
    Call<Map<String, Object>> getEmployeeId(@Query("email") String email);

}
