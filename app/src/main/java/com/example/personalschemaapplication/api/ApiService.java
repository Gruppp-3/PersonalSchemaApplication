package com.example.personalschemaapplication.api;

import com.example.personalschemaapplication.Employee;
import com.example.personalschemaapplication.WorkShift;

import java.util.List;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Employee verification (login)
    @GET("api/employees/verify/{id}")
    Call<Boolean> verifyEmployeeId(@Path("id") Long id);

    // Get employee info
    @GET("api/employees/{id}")
    Call<Employee> getEmployeeById(@Path("id") Long id);

    // Get all work shifts
    @GET("api/workshifts")
    Call<List<WorkShift>> getAllWorkShifts();

    // Get shifts for specific employee
    @GET("api/workshifts/employee/{employeeId}")
    Call<List<WorkShift>> getWorkShiftsByEmployee(@Path("employeeId") Long employeeId);

    // Get unassigned shifts
    @GET("api/workshifts/unassigned")
    Call<List<WorkShift>> getUnassignedWorkShifts();

    // Assign shift to employee
    @PUT("api/workshifts/{id}/assign/{employeeId}")
    Call<WorkShift> assignShiftToEmployee(@Path("id") Long id, @Path("employeeId") Long employeeId);
}