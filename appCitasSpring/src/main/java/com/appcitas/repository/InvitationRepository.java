package com.appcitas.repository;

import com.appcitas.model.Invitation;
import com.appcitas.model.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findByToUserIdAndStatus(Long toUserId, InvitationStatus status);
}
