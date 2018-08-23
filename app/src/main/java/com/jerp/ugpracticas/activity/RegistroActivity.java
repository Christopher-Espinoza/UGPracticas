package com.jerp.ugpracticas.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jerp.ugpracticas.R;
import com.jerp.ugpracticas.database.FireBaseReferencia;
import com.jerp.ugpracticas.entity.Carrera;
import com.jerp.ugpracticas.entity.Docente;
import com.jerp.ugpracticas.entity.Estudiante;
import com.jerp.ugpracticas.entity.Facultad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RegistroActivity extends AppCompatActivity {

    private TextInputLayout tilnombre, tilapellido, tiltelefono, tilcedula, tilemail, tilfnacimiento, tilcontra, tilproyecto, tildocente;
    private Spinner scarrera, sfacultad;
    private RadioGroup rbggenero;
    private com.github.clans.fab.FloatingActionButton fab;
    private DatabaseReference UGPracticasBDReference;
    private FirebaseDatabase UGPracticasBD;
    private StorageReference UGPtacticasStorage;
    private int dia, mes, año;
    private FirebaseAuth UGPracticasAuth;
    private FirebaseAuth.AuthStateListener UGPracticasListener;
    private ProgressDialog progressDialog;
    private final String raiz = "UGpracticas/";
    private final String ruta = raiz + "mispruebas";
    private final int cod_selecciona = 10;
    private final int cod_foto = 20;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String path = "", login;
    private ImageView imagen;
    private List<Facultad> facultades;
    private List<Carrera> carreras;
    private List<String> facu;
    private List<String> carre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = findViewById(R.id.toolbarregistro);
        setSupportActionBar(toolbar);

        init();
        getExtras();
        getFacultad();
        setonclicklistener();
        Authenticacion();

        if (validapermiso()) {
            imagen.setEnabled(true);
        } else {
            imagen.setEnabled(false);
        }
    }

    private void getCarrera(final String facultad) {
        UGPracticasBDReference.child(FireBaseReferencia.CARRERA).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carreras.clear();
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    Carrera carrera = snap.getValue(Carrera.class);
                    if (facultad.equals(carrera.getFacultad())){
                        carreras.add(carrera);
                        carre.add(carreras.toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegistroActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carre);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scarrera.setAdapter((SpinnerAdapter) carreras);
        progressDialog.dismiss();
    }

    private void getFacultad() {
        UGPracticasBDReference.child(FireBaseReferencia.FACULTAD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                facultades.clear();
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    Facultad facultad = snap.getValue(Facultad.class);
                    facultades.add(facultad);
                    facu.add(facultades.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegistroActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facu);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sfacultad.setAdapter(adapter);

        sfacultad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressDialog.setMessage("Cargando carreras...");
                progressDialog.show();
                getCarrera(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void Authenticacion() {
        UGPracticasListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = UGPracticasAuth.getCurrentUser();
                if (user != null) {
                    openAccount();
                }
            }
        };
    }

    private void setonclicklistener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userregister();
            }
        });

        tilfnacimiento.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadcalendar(v);
            }
        });

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarimagen();
            }
        });
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            login = extras.getString("loginhow");
        }
    }

    private void init() {
        tilnombre = findViewById(R.id.tilnombre);
        tilapellido = findViewById(R.id.tilapellido);
        tilfnacimiento = findViewById(R.id.tilfnacimiento);
        rbggenero = findViewById(R.id.rbggenero);
        tiltelefono = findViewById(R.id.tiltelefono);
        tilcedula = findViewById(R.id.tilcedula);
        tilemail = findViewById(R.id.tilemail);
        tilcontra = findViewById(R.id.tilcontrasena);
        scarrera = findViewById(R.id.scarrera);
        sfacultad = findViewById(R.id.sfacultad);
        tilproyecto = findViewById(R.id.tilproyecto);
        tildocente = findViewById(R.id.tildocente);
        imagen = findViewById(R.id.ivfotoperfil);
        fab = findViewById(R.id.fabregistro);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);

        UGPracticasBD = FirebaseDatabase.getInstance();
        UGPracticasBDReference = UGPracticasBD.getReference();
        UGPracticasAuth = FirebaseAuth.getInstance();
        UGPtacticasStorage = FirebaseStorage.getInstance().getReference(FireBaseReferencia.ESTUDIANTE);
    }

    private boolean validapermiso() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }

        if ((checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
        }
        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegistroActivity.this);
        dialog.setTitle("Permisos Desactivados");
        dialog.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la app");

        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                imagen.setEnabled(true);
            } else {
                solicitarpermisosmanual();
            }
        }
    }

    private void solicitarpermisosmanual() {
        final CharSequence[] opciones = {"si", "no"};
        final AlertDialog.Builder aleropcionnes = new AlertDialog.Builder(RegistroActivity.this);
        aleropcionnes.setTitle("¿Desea configurar lso permisos de forma manual?");
        aleropcionnes.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("si")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Los permisos no fueron aceptados", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        aleropcionnes.show();
    }

    private void cargarimagen() {
        final CharSequence[] opciones = {"Tomar Foto", "Cargar Imagen", "Cancelar"};
        final AlertDialog.Builder aleropcionnes = new AlertDialog.Builder(RegistroActivity.this);
        aleropcionnes.setTitle("Seleccionar una Opcion");
        aleropcionnes.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("Tomar Foto")) {
                    Tomarfoto();
                } else {
                    if (opciones[which].equals("Cargar Imagen")) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Selecciona la aplicacion"), cod_selecciona);
                    } else {
                        dialog.dismiss();
                    }
                }
            }
        });
        aleropcionnes.show();
    }

    private void Tomarfoto() {
        File file = new File(Environment.getExternalStorageDirectory(), ruta);
        boolean iscreada = file.exists();
        String nombre = "";
        if (iscreada == false) {
            iscreada = file.mkdirs();
        }

        if (iscreada == true) {
            nombre = (System.currentTimeMillis() / 1000) + ".jpeg";
        }
        path = Environment.getExternalStorageDirectory() + File.separator + ruta + File.separator + nombre;

        File imagen = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case cod_selecciona:
                    Uri mipath = data.getData();
                    imagen.setImageURI(mipath);
                    break;

                case cod_foto:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Toast.makeText(RegistroActivity.this, "Foto guardada en " + path, Toast.LENGTH_SHORT).show();
                                }
                            });

                    break;

                case REQUEST_IMAGE_CAPTURE:
                    String key = getKey();

                    Bundle extras = data.getExtras();
                    Bitmap imagebitmap = (Bitmap) extras.get("data");
                    StorageReference filepath = UGPtacticasStorage.child("UGPracticas").child(key);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagebitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] datas = baos.toByteArray();

                    UploadTask uploadTask = filepath.putBytes(datas);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri uri = taskSnapshot.getUploadSessionUri();
                            Glide.with(RegistroActivity.this).load(uri).into(imagen);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistroActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }
    }

    @NonNull
    private String getKey() {
        String fnombre = tilnombre.getEditText().getText().toString().substring(0, 1);
        String apellidosinespa = tilapellido.getEditText().getText().toString().replace(" ", "-");
        return fnombre.concat(apellidosinespa);
    }

    private void openAccount() {
        Intent intent = new Intent(this, PracticaActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadcalendar(View v) {
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        año = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tilfnacimiento.getEditText().setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        }, dia, mes, año);
        datePickerDialog.show();
    }

    private void userregister() {
        final String nombre = tilnombre.getEditText().getText().toString();
        final String apellido = tilapellido.getEditText().getText().toString();
        final String fnacmimiento = tilfnacimiento.getEditText().getText().toString();

        int rbid = rbggenero.getCheckedRadioButtonId();
        View rb = rbggenero.findViewById(rbid);
        int indice = rbggenero.indexOfChild(rb);
        RadioButton rbgenero = (RadioButton) rbggenero.getChildAt(indice);
        final String genero = rbgenero.getText().toString();

        final String telefono = tiltelefono.getEditText().getText().toString();
        final String cedula = tilcedula.getEditText().getText().toString();
        final String email = tilemail.getEditText().getText().toString();
        final String contra = tilcontra.getEditText().getText().toString();
        final String carrera = scarrera.getSelectedItem().toString();
        final String facultad = sfacultad.getSelectedItem().toString();
        final String proyecto = tilproyecto.getEditText().getText().toString();
        final String docenteest = tildocente.getEditText().getText().toString();

        UGPtacticasStorage.child("UGPracticas").child(getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (login == "Estudiante") {
                    Estudiante estudiante = new Estudiante(nombre, apellido, fnacmimiento,
                            genero, telefono, cedula, email, contra, carrera, facultad, proyecto, docenteest, uri.toString());

                    String fnombre = nombre.substring(0, 1);
                    String apellidosinespa = apellido.replace(" ", "-");
                    String key = fnombre.concat(apellidosinespa);

                    UGPracticasBDReference.child(FireBaseReferencia.ESTUDIANTE).child(key).setValue(estudiante);

                    if (!email.isEmpty() && !contra.isEmpty()) {
                        progressDialog.setMessage("Registrando");
                        progressDialog.show();
                        UGPracticasAuth.createUserWithEmailAndPassword(email, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegistroActivity.this, "Error al registrarse " + task.getResult(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(RegistroActivity.this, PracticaActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                } else {
                    Docente docente = new Docente(nombre, apellido, fnacmimiento,
                            genero, telefono, cedula, email, contra, carrera, facultad, uri.toString());

                    String fdnombre = nombre.substring(0, 1);
                    String dapellidosinespa = apellido.replace(" ", "-");
                    String dkey = fdnombre.concat(dapellidosinespa);

                    UGPracticasBDReference.child(FireBaseReferencia.DOCENTE).child(dkey).setValue(docente);
                    if (!email.isEmpty() && !contra.isEmpty()) {
                        progressDialog.setMessage("Registrando");
                        progressDialog.show();
                        UGPracticasAuth.createUserWithEmailAndPassword(email, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegistroActivity.this, "Error al registrarse " + task.getResult(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(RegistroActivity.this, DocenteActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
            }
        });
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
