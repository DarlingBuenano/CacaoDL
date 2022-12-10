package software.cacaodl.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

import software.cacaodl.MainActivity;
import software.cacaodl.R;

public class EstadoSalud extends Fragment {
    public Bitmap imagen;
    private static final String ARG_PARAM1 = "fragment Estado de Salud";
    private String[] etiquetas;
    private float[] probabilidades;

    private ImageView imgEstadoSalud;

    private TextView txtProbabilidad_0;
    private TextView txtProbabilidad_1;
    private TextView txtProbabilidad_2;

    //private Button btnVerDetalles;
    //private Button btnIntentarDeNuevo;

    public EstadoSalud() {
        // Required empty public constructor
    }

    public EstadoSalud(Bitmap img) {
        this.imagen = img;
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
        if (getArguments() != null) {
            etiquetas = new String[3];
            etiquetas[0] = "Sana";
            etiquetas[1] = "Monilia";
            etiquetas[2] = "Fitoptora";

            probabilidades = new float[3];
            probabilidades[0] = getArguments().getFloat("Sana");
            probabilidades[1] = getArguments().getFloat("Monilia");
            probabilidades[2] = getArguments().getFloat("Fito");
        }
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
        //ordenarScoreMayorAMenor();

        Locale español = new Locale("es");
        NumberFormat format = NumberFormat.getPercentInstance(español);
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(2);
        String cadena_0 = etiquetas[0] + " ............... " + format.format(probabilidades[0]);
        String cadena_1 = etiquetas[1] + " .......... " + format.format(probabilidades[1]);
        String cadena_2 = etiquetas[2] + " ........ " + format.format(probabilidades[2]);
        txtProbabilidad_0.setText(cadena_0);
        txtProbabilidad_1.setText(cadena_1);
        txtProbabilidad_2.setText(cadena_2);

        imgEstadoSalud.setImageBitmap(this.imagen);
    }

    private void ordenarScoreMayorAMenor() {
        for (int x = 0; x < probabilidades.length; x++) {
            float val_01 = probabilidades[x];
            String val_02 = etiquetas[x];
            int pos = x;
            for (int y = 0; y < probabilidades.length; y++) {
                if (val_01 < probabilidades[y]) {
                    float temp_01 = probabilidades[y];
                    probabilidades[y] = val_01;
                    probabilidades[pos] = temp_01;

                    String temp_02 = etiquetas[y];
                    etiquetas[y] = val_02;
                    etiquetas[pos] = temp_02;

                    pos = y;
                }
            }
        }
    }
}