package com.example.appcitas.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitas.R
import com.example.appcitas.model.Cita

class CitasAdapter(private var citas: List<Cita>) : RecyclerView.Adapter<CitasAdapter.CitaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(citas[position])
    }

    override fun getItemCount(): Int = citas.size

    fun updateData(newCitas: List<Cita>) {
        citas = newCitas
        notifyDataSetChanged()
    }

    inner class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloCita)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionCita)
        private val tvDinero: TextView = itemView.findViewById(R.id.tvChipDinero)
        private val tvIntensidad: TextView = itemView.findViewById(R.id.tvChipIntensidad)
        private val tvCercania: TextView = itemView.findViewById(R.id.tvChipCercania)
        private val tvTemporada: TextView = itemView.findViewById(R.id.tvChipTemporada)

        fun bind(cita: Cita) {
            tvTitulo.text = cita.titulo
            tvDescripcion.text = cita.descripcion

            // Aquí puedes mapear los valores numéricos a texto si lo deseas
            tvDinero.text = "€ ${mapDinero(cita.dinero)}"
            tvIntensidad.text = mapIntensidad(cita.intensidad)
            tvCercania.text = mapCercania(cita.cercania)
            tvTemporada.text = mapTemporada(cita.temporada)
        }

        private fun mapDinero(valor: Int?): String = when (valor) {
            1 -> "Bajo"
            2 -> "Medio"
            3 -> "Alto"
            else -> "N/A"
        }

        private fun mapIntensidad(valor: Int?): String = when (valor) {
            1 -> "Tranqui"
            2 -> "Normal"
            3 -> "Intenso"
            else -> "N/A"
        }

        private fun mapCercania(valor: Int?): String = when (valor) {
            1 -> "Cerca"
            2 -> "Normal"
            3 -> "Lejos"
            else -> "N/A"
        }

        private fun mapTemporada(valor: Int?): String = when (valor) {
            1 -> "Baja"
            2 -> "Media"
            3 -> "Alta"
            else -> "N/A"
        }
    }
}
