package com.jerp.ugpracticas.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jerp.ugpracticas.R;
import com.jerp.ugpracticas.activity.PracticaRegistroActivity;
import com.jerp.ugpracticas.entity.Reporte;

import java.util.List;

public class PracticaAdapter extends RecyclerView.Adapter<PracticaAdapter.PracticasViewHolder> {

    private List<Reporte> reportes;

    public PracticaAdapter(List<Reporte> reportes) {
        this.reportes = reportes;
    }

    @NonNull
    @Override
    public PracticasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_practica, parent, false);
        return new PracticasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PracticasViewHolder holder, int position) {
        Reporte reporte = reportes.get(position);
        holder.tvusuario.setText(reporte.getUsuario());
        holder.tvfecha.setText(reporte.getFecha());
        holder.tvpregunta1.setText(reporte.getQueactividad());
        holder.tvpregunta2.setText(reporte.getCualbeneficio());
        holder.tvpregunta3.setText(reporte.getCuantosbeneficios());
        holder.tvhentrada.setText(reporte.getHoraentrada());
        holder.tvhsalida.setText(reporte.getHorasalida());
        holder.tvfotoentrada.setText(reporte.getFotoentrada());
        holder.tvfotosalida.setText(reporte.getHorasalida());
        if (reporte.getFotoentrada() != null) {
            Glide.with(holder.context).load(reporte.getFotoentrada()).into(holder.iventrada);
        }
        if (reporte.getFotosalida() != null){
            Glide.with(holder.context).load(reporte.getFotosalida()).into(holder.ivsalida);
        }
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return reportes.size();
    }


    public static class PracticasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvfecha, tvpregunta1, tvpregunta2, tvpregunta3, tvusuario, tvhentrada, tvhsalida, tvfotoentrada, tvfotosalida;
        ImageView iventrada, ivsalida;
        Button btndata;
        Context context;

        PracticasViewHolder(View v) {
            super(v);
            context = v.getContext();
            tvusuario = v.findViewById(R.id.tvusuariopractica);
            tvfotoentrada = v.findViewById(R.id.tvfotoentrada);
            tvfotosalida = v.findViewById(R.id.tvfotosalida);
            tvfecha = v.findViewById(R.id.cardtvfecha);
            tvhentrada = v.findViewById(R.id.cardhentrada);
            tvhsalida = v.findViewById(R.id.cardhsalida);
            tvpregunta1 = v.findViewById(R.id.cardtvpregunta1);
            tvpregunta2 = v.findViewById(R.id.cardtvpregunta2);
            tvpregunta3 = v.findViewById(R.id.cardtvpregunta3);
            btndata = v.findViewById(R.id.btndata);
        }

        void setOnClickListener(){
            btndata.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PracticaRegistroActivity.class);
            intent.putExtra("fecha", tvfecha.getText().toString());
            intent.putExtra("pregunta1", tvpregunta1.getText().toString());
            intent.putExtra("pregunta2", tvpregunta2.getText().toString());
            intent.putExtra("pregunta3", tvpregunta3.getText().toString());
            intent.putExtra("entrada", tvhentrada.getText().toString());
            intent.putExtra("salida", tvhsalida.getText().toString());
            intent.putExtra("fotoentrada", tvfotoentrada.getText().toString());
            intent.putExtra("fotosalida", tvfotosalida.getText().toString());
            intent.putExtra("usuario", tvusuario.getText().toString());
            context.startActivity(intent);
        }
    }
}
