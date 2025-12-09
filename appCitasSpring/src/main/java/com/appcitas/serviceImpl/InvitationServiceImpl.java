package com.appcitas.serviceImpl;

import com.appcitas.dto.InvitationMapper;
import com.appcitas.dto.InvitationResponse;
import com.appcitas.dto.SendInvitationRequest;
import com.appcitas.model.Invitation;
import com.appcitas.repository.InvitationRepository;
import com.appcitas.service.InvitationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvitationServiceImpl implements InvitationService {

    private static final Logger log = LoggerFactory.getLogger(InvitationServiceImpl.class);

    private final InvitationRepository invitationRepository;

    public InvitationServiceImpl(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @Override
    public void sendInvitation(SendInvitationRequest request) {
        log.info("Recibida solicitud de invitacion para el email=\"{}\"", request.getToEmail());

        Invitation invitation = new Invitation();
        invitation.setFromUserId(request.getFromUserId());
        invitation.setToUserId(request.getToUserId());
        invitation.setToEmail(request.getToEmail());
        invitation.setStatus("PENDING"); // también lo pone @PrePersist por si acaso

        Invitation saved = invitationRepository.save(invitation);

        log.info("Invitación guardada con id={} para email=\"{}\"",
                saved.getId(), saved.getToEmail());
    }

    @Override
    public List<InvitationResponse> getPendingInvitations(Long userId) {
        List<Invitation> invitations = invitationRepository.findByToUserIdAndStatus(userId, "PENDING");

        return invitations.stream()
                .map(InvitationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InvitationResponse acceptInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NoSuchElementException("Invitation not found"));

        invitation.setStatus("ACCEPTED");
        Invitation saved = invitationRepository.save(invitation);

        return InvitationMapper.toResponse(saved);
    }

    @Override
    public InvitationResponse rejectInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NoSuchElementException("Invitation not found"));

        invitation.setStatus("REJECTED");
        Invitation saved = invitationRepository.save(invitation);

        return InvitationMapper.toResponse(saved);
    }
}
