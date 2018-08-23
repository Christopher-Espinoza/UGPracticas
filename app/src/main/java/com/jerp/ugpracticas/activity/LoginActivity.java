package com.jerp.ugpracticas.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jerp.ugpracticas.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilusuario, tilcontrasena;
    private Button btn_login, btn_registrarse;
    private RadioGroup rglogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth UGPracticasAuth;
    private FirebaseAuth.AuthStateListener UGPracticasListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        Autenticacion();
        setonclicklistener();
    }

    private void setonclicklistener() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterAccount();
            }
        });
        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentr = new Intent(getApplicationContext(), RegistroActivity.class);
                intentr.putExtra("loginhow", getLoginComo());
                startActivity(intentr);
            }
        });
    }

    private void Autenticacion() {
        UGPracticasAuth = FirebaseAuth.getInstance();
        UGPracticasListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser UGPracticasUser = UGPracticasAuth.getCurrentUser();
                if(UGPracticasUser == null){
                    Toast.makeText(LoginActivity.this, "Inicie Sesion por favor ", Toast.LENGTH_SHORT).show();
                }else{
                    openAccount();
                    Toast.makeText(LoginActivity.this, "Bienvenido ", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);

        rglogin = findViewById(R.id.rglogin);
        tilusuario = findViewById(R.id.tilusuario);
        tilcontrasena = findViewById(R.id.tilcontrasena);
        btn_login = findViewById(R.id.btn_login);
        btn_registrarse = findViewById(R.id.btn_registrarse);
    }

    private void enterAccount() {
        String email = tilusuario.getEditText().getText().toString();
        final String pass = tilcontrasena.getEditText().getText().toString();
        if(!email.isEmpty() && !pass.isEmpty()){
            UGPracticasAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        progressDialog.setMessage("Iniciando Sesion");
                        progressDialog.show();
                        openAccount();
                    }else{
                        Toast.makeText(LoginActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void openAccount() {
        if (getLoginComo().equals("Estudiante")){
            Intent iPracticas = new Intent(this, PracticaActivity.class);
            iPracticas.putExtra("usuario", tilusuario.getEditText().getText().toString());
            progressDialog.dismiss();
            startActivity(iPracticas);
        }else{
            Intent iPracticas = new Intent(this, DocenteActivity.class);
            iPracticas.putExtra("usuario", tilusuario.getEditText().getText().toString());
            progressDialog.dismiss();
            startActivity(iPracticas);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        UGPracticasAuth.addAuthStateListener(UGPracticasListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(UGPracticasListener != null){
            UGPracticasAuth.removeAuthStateListener(UGPracticasListener);
        }
    }

    public String getLoginComo() {
        int rbid = rglogin.getCheckedRadioButtonId();
        View rb = rglogin.findViewById(rbid);
        int indice = rglogin.indexOfChild(rb);
        RadioButton rblogin = (RadioButton) rglogin.getChildAt(indice);
        String login = rblogin.getText().toString();
        System.out.println(login);
        return login;
    }
}
