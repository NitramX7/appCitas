package com.appcitas.service;

import com.appcitas.dto.InvitationResponse;
import com.appcitas.dto.SendInvitationRequest;

import java.util.List;

public interface InvitationService {

    void sendInvitation(SendInvitationRequest request);

    List<InvitationResponse> getPendingInvitations(Long userId);

    InvitationResponse acceptInvitation(Long invitationId);

    InvitationResponse rejectInvitation(Long invitationId);
}
