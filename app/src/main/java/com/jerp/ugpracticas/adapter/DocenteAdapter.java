package com.jerp.ugpracticas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jerp.ugpracticas.R;
import com.jerp.ugpracticas.entity.Docente;
import com.jerp.ugpracticas.entity.Estudiante;

import java.util.List;

public class DocenteAdapter extends RecyclerView.Adapter<DocenteAdapter.FotosViewHolder> {

    List<Estudiante> estudiantes;

    public DocenteAdapter(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    @Override
    public FotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_docente, parent, false);
        FotosViewHolder fotosViewHolder = new FotosViewHolder(view);
        return fotosViewHolder;
    }

    @Override
    public void onBindViewHolder(FotosViewHolder holder, int position) {
        Estudiante estudiante = estudiantes.get(position);
        holder.tvnombre.setText(estudiante.getNombre());
        holder.tvapellido.setText(estudiante.getApellido());
        holder.tvtelefono.setText(estudiante.getTelefono());
        holder.tvemail.setText(estudiante.getCorreo());
        holder.tvfacultad.setText(estudiante.getFacultad());
        holder.tvcarrera.setText(estudiante.getCarrera());
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class FotosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvnombre, tvapellido, tvtelefono, tvemail, tvcarrera, tvfacultad;
        Context context;
        Button iblogin;

        public FotosViewHolder(View v) {
            super(v);
            context = v.getContext();
            tvnombre = v.findViewById(R.id.nombreest);
            tvapellido = v.findViewById(R.id.apellidoest);
            tvtelefono = v.findViewById(R.id.telefonoest);
            tvemail = v.findViewById(R.id.telefonoest);
            tvcarrera = v.findViewById(R.id.carreraest);
            tvfacultad = v.findViewById(R.id.facultadest);
            iblogin = v.findViewById(R.id.ibelogin);
        }

        public void setOnClickListener(){
            iblogin.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
