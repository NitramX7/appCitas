data class SendInvitationRequest(
    val fromUserId: Long,
    val toUserId: Long?,
    val toEmail: String
)
