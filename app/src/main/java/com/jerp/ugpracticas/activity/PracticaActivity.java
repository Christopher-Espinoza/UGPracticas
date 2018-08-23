package com.jerp.ugpracticas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jerp.ugpracticas.R;
import com.jerp.ugpracticas.adapter.PracticaAdapter;
import com.jerp.ugpracticas.database.FireBaseReferencia;
import com.jerp.ugpracticas.entity.Reporte;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PracticaActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvusuariolog;
    private RecyclerView recycler;
    private Toolbar toolbar;
    private List<Reporte> reportes;
    private PracticaAdapter adapter;
    private String usuario;
    private int dia, mes, año, hora, minutos;
    private FirebaseDatabase UGPracticasBD;
    private DatabaseReference UGPracticasReference;
    private FirebaseAuth UGPracticasAuth;
    private FirebaseAuth.AuthStateListener UGPracticasListener;
    private com.github.clans.fab.FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practica);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        getExtras();
        getReportes();
        setonclicklistener();

        Autenticacion();

    }

    private void Autenticacion() {
        UGPracticasAuth = FirebaseAuth.getInstance();
        UGPracticasListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = UGPracticasAuth.getCurrentUser();
                if (user == null){
                    openlogin();
                }
            }
        };
    }

    private void setonclicklistener() {
        fab.setOnClickListener(this);
    }

    private void getReportes() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        reportes = new ArrayList<>();

        UGPracticasBD = FirebaseDatabase.getInstance();
        UGPracticasReference = UGPracticasBD.getReference();

        adapter = new PracticaAdapter(reportes);
        recycler.setAdapter(adapter);

        UGPracticasReference.child(FireBaseReferencia.REPORTE).child(generatekey(tvusuariolog.getText().toString()))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportes.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Reporte reporte = snap.getValue(Reporte.class);
                    reportes.add(reporte);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PracticaActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            usuario = extras.getString("usuario");
            tvusuariolog.setText(usuario);
        }
    }

    private void init() {
        recycler = findViewById(R.id.recycler);
        tvusuariolog = findViewById(R.id.tvusuario);
        fab = findViewById(R.id.fabpractica);

        UGPracticasBD = FirebaseDatabase.getInstance();
        UGPracticasReference = UGPracticasBD.getReference();
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

    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH)+1;
        año = c.get(Calendar.YEAR);
        String fecha = dia+"-"+mes+"-"+año;
        String usuario = tvusuariolog.getText().toString();
        System.out.println("usuario registro: "+usuario);
        Reporte reportefecha = new Reporte(fecha, usuario);
        UGPracticasReference.child(FireBaseReferencia.REPORTE).child(generatekey(usuario)).child(generatekey(usuario)+"-"+fecha)
                .setValue(reportefecha);
    }

    private void openlogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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

    public void singout(MenuItem item) {
        UGPracticasAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
