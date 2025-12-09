package com.appcitas.serviceImpl;

import com.appcitas.dto.InvitationMapper;
import com.appcitas.dto.InvitationResponse;
import com.appcitas.dto.SendInvitationRequest;
import com.appcitas.model.Invitation;
import com.appcitas.model.InvitationStatus;
import com.appcitas.model.Usuario;
import com.appcitas.repository.InvitationRepository;
import com.appcitas.repository.UsuarioRepo;
import com.appcitas.service.InvitationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvitationServiceImpl implements InvitationService {

    private final UsuarioRepo usuarioRepo;

    private final InvitationRepository invitationRepository;

    public InvitationServiceImpl(InvitationRepository invitationRepository, UsuarioRepo usuarioRepo) {
        this.invitationRepository = invitationRepository;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public void sendInvitation(SendInvitationRequest request) {

        // 1. Usuario que ENVÍA (fromUserId obligatorio)
        Usuario fromUsuario = usuarioRepo.findById(request.getFromUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario emisor no encontrado"));

        // 2. Usuario que RECIBE (opcional)
        Usuario toUsuario = null;

        if (request.getToUserId() != null) {
            // Si viene id → buscamos por id
            toUsuario = usuarioRepo.findById(request.getToUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario destino no encontrado"));
        } else if (request.getToEmail() != null && !request.getToEmail().isEmpty()) {
            // Si no viene id → intentamos localizarlo por email
            toUsuario = usuarioRepo.findByEmail(request.getToEmail())
                    .orElse(null); // puede no existir aún, no pasa nada
        }

        // 3. Crear invitación
        Invitation invitation = new Invitation();
        invitation.setFromUserId(fromUsuario.getId()); // usa aquí el nombre de tu campo real
        invitation.setToUserId(toUsuario != null ? toUsuario.getId() : null); // puede ser null
        invitation.setToEmail(request.getToEmail());
        invitation.setStatus(InvitationStatus.PENDING);

        // 4. Guardar
        invitationRepository.save(invitation);
    }

    @Override
    public List<InvitationResponse> getPendingInvitations(Long userId) {
        List<Invitation> invitations = invitationRepository.findByToUserIdAndStatus(userId, InvitationStatus.PENDING);

        return invitations.stream()
                .map(InvitationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InvitationResponse acceptInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NoSuchElementException("Invitation not found"));

        invitation.setStatus(InvitationStatus.ACCEPTED);
        Invitation saved = invitationRepository.save(invitation);

        return InvitationMapper.toResponse(saved);
    }

    @Override
    public InvitationResponse rejectInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NoSuchElementException("Invitation not found"));

        invitation.setStatus(InvitationStatus.CANCELLED);
        Invitation saved = invitationRepository.save(invitation);

        return InvitationMapper.toResponse(saved);
    }
}