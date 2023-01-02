package software.cacaodl.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

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

    //private Button btnVerDetalles;
    //private Button btnIntentarDeNuevo;
    NumberFormat format;

    ModelTFLite model;
    JSONArray probabilidadesJson;

    public EstadoSalud() {
        // Required empty public constructor
    }

    public EstadoSalud(Bitmap img) {
        this.imagenBitmap = img.copy(Bitmap.Config.ARGB_8888, true);
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
        txtProbabilidad_1 = root.findViewById(R.id.probabilidad_1);
        txtProbabilidad_2 = root.findViewById(R.id.probabilidad_2);
        //btnVerDetalles = root.findViewById(R.id.btn_ver_detalles);
        //btnIntentarDeNuevo = root.findViewById(R.id.btn_intentar_de_nuevo);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Locale español = new Locale("es");
        format = NumberFormat.getPercentInstance(español);
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(2);

        imgEstadoSalud.setImageBitmap(this.imagenBitmap);
        //ejecutarImageClassification();
        //ejecutarObjectDetection();
    }

    private void ejecutarImageClassification() {
        model = new ModelTFLite(getContext(), this.imagenBitmap);
        this.probabilidadesJson = model.inferencia_imageClassification();

        String cadena_0 = "";
        String cadena_1 = "";
        String cadena_2 = "";

        try {
            cadena_0 = this.probabilidadesJson.getJSONObject(0).getString("nombre");
            cadena_0 = cadena_0 + " ..... " + (float)this.probabilidadesJson.getJSONObject(0).getDouble("porcentaje");

            cadena_1 = this.probabilidadesJson.getJSONObject(1).getString("nombre");
            cadena_1 = cadena_1 + " ..... " + (float)this.probabilidadesJson.getJSONObject(1).getDouble("porcentaje");

            cadena_2 = this.probabilidadesJson.getJSONObject(2).getString("nombre");
            cadena_2 = cadena_2 + " ..... " + (float)this.probabilidadesJson.getJSONObject(2).getDouble("porcentaje");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtProbabilidad_0.setText(cadena_0);
        txtProbabilidad_1.setText(cadena_1);
        txtProbabilidad_2.setText(cadena_2);
    }

    private void ejecutarObjectDetection() {
        model = new ModelTFLite(getContext(), this.imagenBitmap);
        this.probabilidadesJson = model.inferencia_objectdetection();

        String cadena_0 = "";
        String cadena_1 = "";
        String cadena_2 = "";

        try {
            cadena_0 = this.probabilidadesJson.getJSONObject(0).getString("objeto");
            cadena_0 = cadena_0 + "..." + this.probabilidadesJson.getJSONObject(0).getString("categoria");
            cadena_0 = cadena_0 + "..." + this.probabilidadesJson.getJSONObject(0).getString("score");

            cadena_1 = this.probabilidadesJson.getJSONObject(1).getString("objeto");
            cadena_1 = cadena_1 + "..." + this.probabilidadesJson.getJSONObject(1).getString("categoria");
            cadena_1 = cadena_1 + "..." + this.probabilidadesJson.getJSONObject(1).getString("score");

            cadena_2 = this.probabilidadesJson.getJSONObject(2).getString("objeto");
            cadena_2 = cadena_2 + "..." + this.probabilidadesJson.getJSONObject(2).getString("categoria");
            cadena_2 = cadena_2 + "..." + this.probabilidadesJson.getJSONObject(2).getString("score");

            for (int i = 0; i < this.probabilidadesJson.length(); i++) {
                float left = (float)this.probabilidadesJson.getJSONObject(i).getDouble("left");
                float top = (float)this.probabilidadesJson.getJSONObject(i).getDouble("top");
                float right = (float)this.probabilidadesJson.getJSONObject(i).getDouble("right");
                float bottom = (float)this.probabilidadesJson.getJSONObject(i).getDouble("bottom");

                dibujarRect(left, top, right, bottom);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtProbabilidad_0.setText(cadena_0);
        txtProbabilidad_1.setText(cadena_1);
        txtProbabilidad_2.setText(cadena_2);
    }

    private void dibujarRect(float left, float top, float right, float bottom) {
        // Crea un Canvas asociado al Bitmap
        Canvas canvas = new Canvas(imagenBitmap);

        // Configura el objeto Paint para dibujar un rectángulo relleno
        Paint paint = new Paint();
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setARGB(100, 18, 181, 52);
        //paint.setColor(Color.RED);

        // Dibuja el rectángulo
        canvas.drawRect(left, top, right, bottom, paint);
    }
}