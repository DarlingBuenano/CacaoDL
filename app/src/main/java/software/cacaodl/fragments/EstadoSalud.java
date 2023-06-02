package software.cacaodl.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import software.cacaodl.R;
import software.cacaodl.modelos.ModelTFLite;

public class EstadoSalud extends Fragment {
    public Bitmap imagenBitmap;
    private static final String ARG_PARAM1 = "fragment Estado de Salud";

    private ImageView imgEstadoSalud;

    private TextView txtProbabilidad_0;
    private TextView txtProbabilidad_1;
    private TextView txtProbabilidad_2;

    private Button btnVerDetalles;
    //private Button btnIntentarDeNuevo;
    NumberFormat format;
    private Uri uriImage;
    private static final int tamaño_imagen = 640;
    private int claseId;

    ModelTFLite model;
    JSONArray probabilidadesJson;

    public EstadoSalud() {
        // Required empty public constructor
    }

    public EstadoSalud(Bitmap img) {
        this.imagenBitmap = img.copy(Bitmap.Config.ARGB_8888, true);
    }

    public EstadoSalud(Uri uriImage) {
        this.uriImage = uriImage;
    }

    public static EstadoSalud newInstance(String param1) {
        EstadoSalud fragment = new EstadoSalud();
        Bundle args = new Bundle();
        args.putString(param1, ARG_PARAM1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_estado_salud, container, false);
        imgEstadoSalud = root.findViewById(R.id.img_estado_salud);
        txtProbabilidad_0 = root.findViewById(R.id.probabilidad_0);

        btnVerDetalles = root.findViewById(R.id.btn_ver_detalles);
        btnVerDetalles.setOnClickListener(onCLicVerDiagnostico);
        //btnIntentarDeNuevo = root.findViewById(R.id.btn_intentar_de_nuevo);
        return root;
    }

    private final View.OnClickListener onCLicVerDiagnostico = view -> {
        Fragment frgDiagnostico = new Diagnostico(claseId);
        abrirFragment(this, frgDiagnostico, "frgDiagnostico");
    };

    @Override
    public void onStart() {
        super.onStart();

        Locale español = new Locale("es");
        format = NumberFormat.getPercentInstance(español);
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(2);

        if (uriImage != null) {
            //imgEstadoSalud.setImageURI(uriImage);
            try {
                imagenBitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uriImage);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                imagenBitmap = Bitmap.createBitmap(imagenBitmap, 0, 0, imagenBitmap.getWidth(), imagenBitmap.getHeight(), matrix, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        imagenBitmap = Bitmap.createScaledBitmap(imagenBitmap, tamaño_imagen, tamaño_imagen, true);
        imgEstadoSalud.setImageBitmap(this.imagenBitmap);
        ejecutarObjectDetection();
    }

    private void ejecutarObjectDetection() {
        model = new ModelTFLite(getContext(), this.imagenBitmap);
        probabilidadesJson = model.inferencia_objectdetection();

        String resultado = "";
        try {
            String clase = probabilidadesJson.getJSONObject(0).getString("categoria");
            switch (clase){
                case "fitoftora":
                    claseId = 0;
                    break;
                case "monilia":
                    claseId = 1;
                    break;
                case "sana":
                    claseId = 3;
                    break;
            }

            for (int i = 0; i < probabilidadesJson.length(); i++) {
                resultado = resultado + "Objeto: 0" + i+1;
                resultado = resultado + "\n    Categoría: " + probabilidadesJson.getJSONObject(i).getString("categoria");
                //String confianza = format.format(probabilidadesJson.getJSONObject(0).getString("confianza"));
                resultado = resultado + "\n    Confianza: " + probabilidadesJson.getJSONObject(i).getString("confianza");
                resultado = resultado + "\n\n";

                float left = (float)probabilidadesJson.getJSONObject(i).getDouble("left");
                float top = (float)probabilidadesJson.getJSONObject(i).getDouble("top");
                float right = (float)probabilidadesJson.getJSONObject(i).getDouble("right");
                float bottom = (float)probabilidadesJson.getJSONObject(i).getDouble("bottom");

                dibujarRect(left, top, right, bottom);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtProbabilidad_0.setText(resultado);
    }

    private void dibujarRect(float left, float top, float right, float bottom) {
        // Crea un Canvas asociado al Bitmap
        Canvas canvas = new Canvas(imagenBitmap);

        // Configura el objeto Paint para dibujar un rectángulo relleno
        Paint paint = new Paint();
        paint.setStrokeWidth(4f);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setARGB(100, 18, 181, 52);
        paint.setColor(Color.GREEN);

        // Dibuja el rectángulo
        canvas.drawRect(left, top, right, bottom, paint);
    }

    private void abrirFragment(Fragment frgActual, Fragment frgPosterior, String tagFrgPosterior) {
        getActivity().getSupportFragmentManager().beginTransaction()
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
}