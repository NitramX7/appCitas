package com.appcitas.controller;

import com.appcitas.dto.InvitationResponse;
import com.appcitas.dto.SendInvitationRequest;
import com.appcitas.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couples/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendInvitation(@RequestBody SendInvitationRequest request) {
        invitationService.sendInvitation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<InvitationResponse>> getPendingInvitations(@PathVariable Long userId) {
        List<InvitationResponse> pending = invitationService.getPendingInvitations(userId);
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<InvitationResponse> acceptInvitation(@PathVariable Long invitationId) {
        InvitationResponse response = invitationService.acceptInvitation(invitationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<InvitationResponse> rejectInvitation(@PathVariable Long invitationId) {
        InvitationResponse response = invitationService.rejectInvitation(invitationId);
        return ResponseEntity.ok(response);
    }
}
