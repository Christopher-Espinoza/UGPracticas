package com.jerp.ugpracticas.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jerp.ugpracticas.R;
import com.jerp.ugpracticas.adapter.DocenteAdapter;
import com.jerp.ugpracticas.adapter.PracticaAdapter;
import com.jerp.ugpracticas.database.FireBaseReferencia;
import com.jerp.ugpracticas.entity.Docente;
import com.jerp.ugpracticas.entity.Estudiante;
import com.jerp.ugpracticas.entity.Reporte;

import java.util.ArrayList;
import java.util.List;

public class DocenteActivity extends AppCompatActivity {

    private TextView tvusuariodocente;
    private List<Estudiante> estudiantes;
    private List<Docente> docentes;
    private DocenteAdapter docenteAdapter;
    private RecyclerView recycler;
    private Toolbar toolbar;
    private FirebaseDatabase UGPracticasBD;
    private DatabaseReference UGPracticasReference;
    private FirebaseAuth UGPracticasAuth;
    private FirebaseAuth.AuthStateListener UGPracticasListener;
    private String usuario, docente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docente);
        toolbar = findViewById(R.id.toolbardocente);
        setSupportActionBar(toolbar);
        init();
        getExtras();
        getEstudiantes();
    }

    private void getEstudiantes() {
        UGPracticasReference.child(FireBaseReferencia.DOCENTE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                docentes.clear();
                for (DataSnapshot snapdocente: dataSnapshot.getChildren()) {
                    Docente docente = snapdocente.getValue(Docente.class);
                    if (docente.getCorreo().equals(tvusuariodocente.getText().toString())){
                        docentes.add(docente);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DocenteActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        recycler = findViewById(R.id.recyclerdocente);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        estudiantes = new ArrayList<>();

        UGPracticasBD = FirebaseDatabase.getInstance();
        UGPracticasReference = UGPracticasBD.getReference();

        docenteAdapter = new DocenteAdapter(estudiantes);
        recycler.setAdapter(docenteAdapter);

        UGPracticasReference.child(FireBaseReferencia.ESTUDIANTE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                estudiantes.clear();
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    Estudiante estudiante = snap.getValue(Estudiante.class);
                    for (int i=0; i<docentes.size(); i++) {
                        if(estudiante.getDocente().equals(docentes.get(i))){
                            estudiantes.add(estudiante);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DocenteActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            usuario = extras.getString("usuario");
            tvusuariodocente.setText(usuario);
        }
    }

    private void init() {
        tvusuariodocente = findViewById(R.id.tvusuariodocente);
    }

    private String generatekey(String key) {
        String keymod = key.substring(0, key.indexOf('@'));
        String keymod2 = keymod.replace(".", "-");
        return key = keymod2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void singout(MenuItem item) {
        UGPracticasAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UGPracticasAuth.addAuthStateListener(UGPracticasListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (UGPracticasListener != null) {
            UGPracticasAuth.removeAuthStateListener(UGPracticasListener);
        }
    }
}
