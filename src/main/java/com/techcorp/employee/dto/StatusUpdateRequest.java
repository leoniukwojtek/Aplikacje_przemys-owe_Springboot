package com.techcorp.employee.dto;


import com.techcorp.employee.model.EmploymentStatus;

public class StatusUpdateRequest {
    private EmploymentStatus status;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(EmploymentStatus status) {
        this.status = status;
    }

    public EmploymentStatus getStatus() {
        return status;
    }

    public void setStatus(EmploymentStatus status) {
        this.status = status;
    }
}
