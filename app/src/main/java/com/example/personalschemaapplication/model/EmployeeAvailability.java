package com.example.personalschemaapplication.model;

public class EmployeeAvailability {
    private Long availabilityId;  // Motsvarar AVAILABILITY_ID (PK i DB)
    private Long employeeId;      // Vilken anställd (FK i DB)

    private String dayOfWeek;     // Exempel: "MONDAY", "TUESDAY", ...
    private String startTime;     // Exempel: "09:00"
    private String endTime;       // Exempel: "17:00"
    private String preference;    // Exempel: "PREFERRED", "AVAILABLE", "UNAVAILABLE"

    // Tom konstruktor (bra för JSON-parsing)
    public EmployeeAvailability() {
    }

    // Konstruktor med alla fält
    public EmployeeAvailability(Long availabilityId, Long employeeId,
                                String dayOfWeek, String startTime,
                                String endTime, String preference) {
        this.availabilityId = availabilityId;
        this.employeeId = employeeId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.preference = preference;
    }

    // Getters & Setters
    public Long getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
}
