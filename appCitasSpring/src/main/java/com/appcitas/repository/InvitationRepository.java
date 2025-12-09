package com.appcitas.repository;

import com.appcitas.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findByToUserIdAndStatus(Long toUserId, String status);
}
