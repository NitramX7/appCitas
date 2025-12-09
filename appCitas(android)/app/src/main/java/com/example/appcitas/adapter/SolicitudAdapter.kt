package com.example.appcitas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitas.R
import com.example.appcitas.model.SolicitudPareja

class SolicitudAdapter(
    private val solicitudes: List<SolicitudPareja>,
    private val onAceptar: (SolicitudPareja) -> Unit,
    private val onRechazar: (SolicitudPareja) -> Unit
) : RecyclerView.Adapter<SolicitudAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvSolicitanteName)
        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)
        val btnRechazar: Button = view.findViewById(R.id.btnRechazar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_solicitud, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val solicitud = solicitudes[position]
        holder.tvName.text = "Solicitud de: ${solicitud.solicitante.username}"
        
        holder.btnAceptar.setOnClickListener { onAceptar(solicitud) }
        holder.btnRechazar.setOnClickListener { onRechazar(solicitud) }
    }

    override fun getItemCount() = solicitudes.size
}
