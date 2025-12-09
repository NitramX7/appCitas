package com.appcitas.dto;

import com.appcitas.model.Invitation;

public class InvitationMapper {

    public static InvitationResponse toResponse(Invitation invitation) {
        return new InvitationResponse(
                invitation.getId(),
                invitation.getFromUserId(),
                invitation.getToUserId(),
                invitation.getToEmail(),
                invitation.getStatus(),
                invitation.getCreatedAt());
    }
}
