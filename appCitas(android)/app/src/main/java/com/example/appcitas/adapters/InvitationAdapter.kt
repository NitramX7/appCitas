package com.example.appcitas.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitas.R
import com.example.appcitas.model.Invitation

interface InvitationActionListener {
    fun onAcceptInvitation(invitation: Invitation)
    fun onRejectInvitation(invitation: Invitation)
}

class InvitationAdapter(
    private val invitations: MutableList<Invitation>,
    private val listener: InvitationActionListener
) : RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invitation, parent, false)
        return InvitationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        val invitation = invitations[position]
        holder.bind(invitation, listener)
    }

    override fun getItemCount(): Int = invitations.size

    fun updateData(newInvitations: List<Invitation>) {
        invitations.clear()
        invitations.addAll(newInvitations)
        notifyDataSetChanged()
    }

    class InvitationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSenderEmail: TextView = itemView.findViewById(R.id.tvSenderEmail)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnReject: Button = itemView.findViewById(R.id.btnReject)

        fun bind(invitation: Invitation, listener: InvitationActionListener) {
            tvSenderEmail.text = "Solicitud de: ${'$'}{invitation.senderEmail}"

            btnAccept.setOnClickListener {
                listener.onAcceptInvitation(invitation)
            }

            btnReject.setOnClickListener {
                listener.onRejectInvitation(invitation)
            }
        }
    }
}
