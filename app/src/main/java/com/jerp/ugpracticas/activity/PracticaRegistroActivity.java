package com.jerp.ugpracticas.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import com.jerp.ugpracticas.entity.Reporte;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PracticaRegistroActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String fecha, pregunta1, pregunta2, pregunta3, usuario, entrada, salida, fotoentrada, fotosalida, path = "";
    private TextInputLayout tilpregunta1, tilpregunta2, tilpregunta3, tilentrada, tilsalida;
    private TextView tvusuario, tvfecha;
    private FloatingActionButton fabguardar, fabmodificar;
    private DatabaseReference UGPracticasBDReference;
    private FirebaseDatabase UGPracticasBD;
    private StorageReference UGPtacticasStorage;
    private FirebaseAuth UGPracticasAuth;
    private ProgressDialog progressDialog;
    private final String raiz = "UGpracticas/";
    private final String ruta = raiz + "mispruebas";
    private final int cod_selecciona = 10;
    private final int cod_foto = 20;
    static final int REQUEST_IMAGE_CAPTURE_ENTRADA = 1;
    static final int REQUEST_IMAGE_CAPTURE_SALIDA = 2;
    private ImageView imagenentrada, imagensalida;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int location;
    private GoogleMap mMap;
    private Toolbar toolbar;
    private List<Marker> markers = new ArrayList<>();
    private List<Marker> realtimemarkes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practica_registro);
        toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        init();
        getExtras();
        getLocation();
        setPreguntas();
        ViewMap();
        countDownTimer();
        setonclickListener();

        if (validapermiso()) {
            imagenentrada.setEnabled(true);
            imagensalida.setEnabled(true);
        } else {
            imagenentrada.setEnabled(false);
            imagensalida.setEnabled(false);
        }

    }

    private void ViewMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.practicamap);
        mapFragment.getMapAsync(this);
    }

    private void setonclickListener() {
        tilentrada.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeClick(v);
            }
        });

        tilsalida.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeClick(v);
            }
        });

        fabmodificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilentrada.setEnabled(true);
                tilsalida.setEnabled(true);
                tilpregunta1.setEnabled(true);
                tilpregunta2.setEnabled(true);
                tilpregunta3.setEnabled(true);
            }
        });

        fabguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fecha = tvfecha.getText().toString();
                String usuario = tvusuario.getText().toString();
                String que_actvidad = tilpregunta1.getEditText().getText().toString();
                String cual_benefecio = tilpregunta2.getEditText().getText().toString();
                String cuantos_benefeciados = tilpregunta3.getEditText().getText().toString();
                String hentrada = tilentrada.getEditText().getText().toString();
                String hsalida = tilsalida.getEditText().getText().toString();

                Map<String, Object> reporte = new HashMap<>();
                reporte.put("queactividad", que_actvidad);
                reporte.put("cualbeneficio", cual_benefecio);
                reporte.put("cuantosbeneficios", cuantos_benefeciados);
                reporte.put("horaentrada", hentrada);
                reporte.put("horansalida", hsalida);
                UGPracticasBDReference.child(FireBaseReferencia.REPORTE).child(generatekey(usuario)).
                        child(generatekey(usuario) + "-" + fecha).updateChildren(reporte);
                Toast.makeText(PracticaRegistroActivity.this, "Datos Guardados Satisfactoriamente", Toast.LENGTH_SHORT).show();
            }
        });

        imagenentrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarimagen();
            }
        });
        imagensalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarimagen();
            }
        });
    }

    private void setPreguntas() {

        if (pregunta1.isEmpty()) {
            tilpregunta1.getEditText().setText("");
        } else {
            tilpregunta1.getEditText().setText(pregunta1);
        }

        if (pregunta2.isEmpty()) {
            tilpregunta2.getEditText().setText("");
        } else {
            tilpregunta2.getEditText().setText(pregunta2);
        }

        if (pregunta3.isEmpty()) {
            tilpregunta3.getEditText().setText("");
        } else {
            tilpregunta3.getEditText().setText(pregunta3);
        }

        if (entrada.isEmpty()) {
            tilentrada.getEditText().setText("");
        } else {
            tilentrada.getEditText().setText(entrada);
        }

        if (salida.isEmpty()) {
            tilsalida.getEditText().setText("");
        } else {
            tilsalida.getEditText().setText(salida);
        }

        if (fotoentrada != null) {
            Glide.with(getApplicationContext()).load(fotoentrada).into(imagenentrada);
        }

        if (fotosalida != null) {
            Glide.with(getApplicationContext()).load(fotosalida).into(imagensalida);
        }
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fecha = extras.getString("fecha");
            pregunta1 = extras.getString("pregunta1");
            pregunta2 = extras.getString("pregunta2");
            pregunta3 = extras.getString("pregunta3");
            entrada = extras.getString("entrada");
            fotoentrada = extras.getString("fotoentrada");
            fotosalida = extras.getString("fotosalida");
            salida = extras.getString("salida");
            usuario = extras.getString("usuario");
            tvusuario.setText(usuario);
            tvfecha.setText(fecha);
        }
    }

    private void init() {
        UGPracticasBD = FirebaseDatabase.getInstance();
        UGPracticasBDReference = UGPracticasBD.getReference();
        UGPtacticasStorage = FirebaseStorage.getInstance().getReference();
        UGPracticasAuth = FirebaseAuth.getInstance();

        tilpregunta1 = findViewById(R.id.tilpregunta1);
        tilpregunta2 = findViewById(R.id.tilpregunta2);
        tilpregunta3 = findViewById(R.id.tilpregunta3);
        tilentrada = findViewById(R.id.tilentrada);
        tilsalida = findViewById(R.id.tilsalida);
        fabguardar = findViewById(R.id.fabguardar);
        fabmodificar = findViewById(R.id.fabmodificar);
        imagenentrada = findViewById(R.id.iventrada);
        imagensalida = findViewById(R.id.ivsalida);
        tvusuario = findViewById(R.id.tvusuariopractica);
        tvfecha = findViewById(R.id.tvfechapractica);
        textdesenable();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
    }

    private void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PracticaRegistroActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, location);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Map<String, Object> reporte = new HashMap<>();
                    reporte.put("latitud", location.getLatitude());
                    reporte.put("longitud", location.getLongitude());

                    UGPracticasBDReference.child(FireBaseReferencia.REPORTE).child(generatekey(usuario))
                            .child(generatekey(usuario) + "-" + fecha).updateChildren(reporte);
                }
            }
        });
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(PracticaRegistroActivity.this);
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
                imagenentrada.setEnabled(true);
                imagensalida.setEnabled(true);
            } else {
                solicitarpermisosmanual();
            }
        }
    }

    private void solicitarpermisosmanual() {
        final CharSequence[] opciones = {"si", "no"};
        final AlertDialog.Builder aleropcionnes = new AlertDialog.Builder(PracticaRegistroActivity.this);
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
        final AlertDialog.Builder aleropcionnes = new AlertDialog.Builder(PracticaRegistroActivity.this);
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

        if (imagenentrada.isSelected()){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagenentrada));
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_ENTRADA);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagenentrada));
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_ENTRADA);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case cod_selecciona:
                    Uri mipath = data.getData();
                    imagenentrada.setImageURI(mipath);
                    break;

                case cod_foto:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Toast.makeText(PracticaRegistroActivity.this, "Foto guardada en " + path, Toast.LENGTH_SHORT).show();
                                }
                            });

                    break;

                case REQUEST_IMAGE_CAPTURE_ENTRADA:
                    String fnombre = tvusuario.getText().toString().substring(0, 1);
                    String apellidosinespa = tvusuario.getText().toString().replace(" ", "-");
                    String key = fnombre.concat(apellidosinespa).concat(tilentrada.getEditText().getText().toString());

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
                            Glide.with(PracticaRegistroActivity.this).load(uri).into(imagenentrada);
                            Toast.makeText(PracticaRegistroActivity.this, "Foto de la hora de entrada subida exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PracticaRegistroActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    UGPtacticasStorage.child("UGPracticas").child(key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> reportefoto = new HashMap<>();
                            reportefoto.put("fotoentrada", uri.toString());
                            UGPracticasBDReference.child(FireBaseReferencia.REPORTE).child(generatekey(usuario)).
                                    child(generatekey(usuario) + "-" + fecha).updateChildren(reportefoto);
                        }
                    });
                    break;
                case REQUEST_IMAGE_CAPTURE_SALIDA:
                    String sfnombre = tvusuario.getText().toString().substring(0, 1);
                    String sapellidosinespa = tvusuario.getText().toString().replace(" ", "-");
                    String skey = sfnombre.concat(sapellidosinespa).concat(tilsalida.getEditText().getText().toString());

                    Bundle sextras = data.getExtras();
                    Bitmap simagebitmap = (Bitmap) sextras.get("data");
                    StorageReference sfilepath = UGPtacticasStorage.child("UGPracticas").child(skey);
                    ByteArrayOutputStream sbaos = new ByteArrayOutputStream();
                    simagebitmap.compress(Bitmap.CompressFormat.JPEG, 100, sbaos);
                    byte[] sdatas = sbaos.toByteArray();

                    UploadTask suploadTask = sfilepath.putBytes(sdatas);
                    suploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri uri = taskSnapshot.getUploadSessionUri();
                            Glide.with(PracticaRegistroActivity.this).load(uri).into(imagensalida);
                            Toast.makeText(PracticaRegistroActivity.this, "Foto de la hora de salida subida exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PracticaRegistroActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    UGPtacticasStorage.child("UGPracticas").child(skey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> reportefoto = new HashMap<>();
                            reportefoto.put("fotosalida", uri.toString());
                            UGPracticasBDReference.child(FireBaseReferencia.REPORTE).child(generatekey(usuario)).
                                    child(generatekey(usuario) + "-" + fecha).updateChildren(reportefoto);
                        }
                    });
                    break;
            }
        }
    }

    private String generatekey(String key) {
        String keymod = key.substring(0, key.indexOf('@'));
        String keymod2 = keymod.replace(".", "-");
        return keymod2;
    }

    private void textdesenable() {
        tilpregunta1.setEnabled(false);
        tilpregunta2.setEnabled(false);
        tilpregunta3.setEnabled(false);
    }

    public void TimeClick(final View v) {
        final Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minutos = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hora = hourOfDay + ":" + minute;
                Map<String, Object> reporte = new HashMap<>();
                switch (v.getId()) {
                    case R.id.tietentrada:
                        reporte.put("horaentrada", hora);
                        tilentrada.getEditText().setText(hora);
                        break;
                    case R.id.tietsalida:
                        reporte.put("horasalida", hora);
                        tilsalida.getEditText().setText(hora);
                        break;
                }
            }
        }, hora, minutos, true);
        timePickerDialog.show();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UGPracticasBDReference.child(FireBaseReferencia.REPORTE).child(generatekey(usuario)).child(generatekey(usuario + "-" + fecha))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (Marker marker : realtimemarkes) {
                            marker.remove();
                        }

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Reporte reporte = snap.getValue(Reporte.class);
                            Double latitud = reporte.getLatitud();
                            Double longitud = reporte.getLongitud();
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(latitud, longitud));
                            markers.add(mMap.addMarker(markerOptions));
                        }

                        realtimemarkes.clear();
                        realtimemarkes.addAll(markers);
                        countDownTimer();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(PracticaRegistroActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void countDownTimer() {
        new CountDownTimer(300000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Toast.makeText(PracticaRegistroActivity.this, "Nueva Ubicación añadida", Toast.LENGTH_SHORT).show();
                onMapReady(mMap);
            }
        }.start();
    }
}