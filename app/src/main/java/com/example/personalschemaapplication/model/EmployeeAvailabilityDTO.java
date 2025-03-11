package com.example.personalschemaapplication.model;

public class EmployeeAvailabilityDTO {
    // Fält kopplade till tillgängligheten
    private Long availabilityId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String preference;

    // Fält kopplade till anställd
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean isAdmin;

    // Tom konstruktor
    public EmployeeAvailabilityDTO() {
    }

    // Exempel på konstruktor som tar in allt
    public EmployeeAvailabilityDTO(Long availabilityId, String dayOfWeek, String startTime, String endTime, String preference,
                                   Long employeeId, String firstName, String lastName, String phoneNumber, Boolean isAdmin) {
        this.availabilityId = availabilityId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.preference = preference;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
    }

    // Getters & Setters
    public Long getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
