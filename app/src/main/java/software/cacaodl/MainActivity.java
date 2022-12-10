package software.cacaodl;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import software.cacaodl.fragments.Camara;
import software.cacaodl.fragments.Diagnostico;
import software.cacaodl.fragments.EstadoSalud;
import software.cacaodl.fragments.Welcome;
import software.cacaodl.modelos.ModelTFLite;
import software.cacaodl.utilidades.Imagen;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_FILE = 2;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Bitmap imagenBitmap;
    ModelTFLite model;
    JSONArray probabilidades;

    private FragmentManager manager;
    private Fragment frgWelcome;
    private Fragment frgEstadoSalud;
    private Fragment frgDiagnostico;
    private Fragment frgCamara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();
        frgWelcome = new Welcome();
        // El método replace ejecuta remove() y add() internamente, eliminando cualquier instancia de fragment abierta.
        manager.beginTransaction().
                replace(R.id.frg_container_view, frgWelcome, "frgWelcome").
                commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pref = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean("sesion", true);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        System.exit(0);
    }

    private void guardarFoto() {
        Imagen imagen = new Imagen(this, this.imagenBitmap);
        imagen.guardarFoto();
    }

    private void ejecutarDeepLearning() {
        model = new ModelTFLite(getApplicationContext(), this.imagenBitmap);
        this.probabilidades = model.inferencia();
    }

    private void abrirEstadoSalud() {
        frgEstadoSalud = new EstadoSalud(imagenBitmap);
        Bundle args = new Bundle();
        try {
            args.putFloat(
                    this.probabilidades.getJSONObject(0).getString("nombre"),
                    (float)this.probabilidades.getJSONObject(0).getDouble("porcentaje"));
            args.putFloat(
                    this.probabilidades.getJSONObject(1).getString("nombre"),
                    (float)this.probabilidades.getJSONObject(1).getDouble("porcentaje"));
            args.putFloat(
                    this.probabilidades.getJSONObject(2).getString("nombre"),
                    (float)this.probabilidades.getJSONObject(2).getDouble("porcentaje"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        frgEstadoSalud.setArguments(args);
        abrirFragment(frgWelcome, frgEstadoSalud, "frgEstadoSalud");
        guardarFoto();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clicTomarUnaFoto(View view) {
        frgCamara = new Camara();
        abrirFragment(frgWelcome, frgCamara, "frgCamara");
        /*try{
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            }
        } catch (IllegalStateException ex) {
            Toast.makeText(this, "Hubo un error al abrir la cámara del dispositivo", Toast.LENGTH_SHORT).show();
            System.out.println(ex);
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clicSubirFotoDesdeGaleria(View view) {
        System.out.println("clicSubirFotoDesdeGaleria");
        Intent camaraInten = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(camaraInten, REQUEST_SELECT_FILE);
        System.out.println("abriendo onActivityResult para subir foto");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("onActivityResult(....)");
        System.out.println("requestCode:"+requestCode +", resultCode:"+resultCode +", data:"+(data!=null));
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            imagenBitmap = null;
            imagenBitmap = (Bitmap) data.getExtras().get("data");
        }
        else if (requestCode == REQUEST_SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imagenSeleccUri = null;
                imagenSeleccUri = data.getData();
                String seleccionarPath = imagenSeleccUri.getPath();

                if (seleccionarPath != null) {
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(imagenSeleccUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imagenBitmap = null;
                    imagenBitmap = BitmapFactory.decodeStream(imageStream);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

        ejecutarDeepLearning();
        abrirEstadoSalud();
    }

    public void clicVerDetalles(View view) {
        frgDiagnostico = new Diagnostico();
        abrirFragment(frgEstadoSalud, frgDiagnostico, "frgDiagnostico");
    }

    public void clicIntentarDeNuevo(View view) {
        regresarAlFragmentAnterior(frgEstadoSalud, frgWelcome, true);
    }

    private void regresarAlFragmentAnterior(Fragment frgActual, Fragment frgAnterior, boolean eliminarActual) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_left_to_rigth,
                R.anim.exit_left_to_rigth,
                R.anim.enter_rigth_to_left,
                R.anim.exit_rigth_to_left);
        transaction.hide(frgActual);
        transaction.show(frgAnterior);
        if (eliminarActual) {
            transaction.remove(frgActual);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }
    private void abrirFragment(Fragment frgActual, Fragment frgPosterior, String tagFrgPosterior) {
        manager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_rigth_to_left,
                        R.anim.exit_rigth_to_left,
                        R.anim.enter_left_to_rigth,
                        R.anim.exit_left_to_rigth)
                .hide(frgActual)
                .add(R.id.frg_container_view, frgPosterior, tagFrgPosterior)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
    // Diagnostico es el último fragment, por tanto no se regresa ahí OBVIAMENTE
}