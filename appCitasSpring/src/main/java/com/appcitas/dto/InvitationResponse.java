package com.appcitas.dto;

import java.time.LocalDateTime;

import com.appcitas.model.InvitationStatus;

public class InvitationResponse {

    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String toEmail;
    private InvitationStatus status;
    private LocalDateTime createdAt;

    public InvitationResponse() {
    }

    public InvitationResponse(Long id, Long fromUserId, Long toUserId,
            String toEmail, InvitationStatus status,
            LocalDateTime createdAt) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.toEmail = toEmail;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ==== Getters y Setters ====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
