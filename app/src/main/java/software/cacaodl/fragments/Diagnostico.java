package software.cacaodl.fragments;

import android.annotation.SuppressLint;
import android.media.MediaParser;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import software.cacaodl.R;

public class Diagnostico extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private ImageButton btnRegresarAEstadoSalud;
    private TextView btnNoEsElResultado;
    private TextView txt_titulo_estado_salud;
    private TextView txt_hongo_estado_salud;
    private TextView txt_diagnostico;
    private TextView txt_sintomas;
    private TextView txt_medidas_prevencion;
    private ImageView img_estado_salud_carouse1;
    private ImageView img_estado_salud_carouse2;
    private ImageView img_estado_salud_carouse3;
    private int claseId = 0;

    public Diagnostico() {
        // Required empty public constructor
    }

    public Diagnostico(int claseId) {
        this.claseId = claseId;
    }

    public static Diagnostico newInstance(String param1, String param2) {
        Diagnostico fragment = new Diagnostico();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diagnostico, container, false);
        btnRegresarAEstadoSalud = root.findViewById(R.id.btn_atras_frg_diag);
        btnRegresarAEstadoSalud.setOnClickListener(onClicRegresarAEstadoSalud);

        btnNoEsElResultado = root.findViewById(R.id.btn_no_es_el_resultado_frg_diag);
        btnNoEsElResultado.setOnClickListener(onClicNoEsElResultado);

        txt_titulo_estado_salud = root.findViewById(R.id.txt_titulo_estado_salud);
        txt_hongo_estado_salud = root.findViewById(R.id.txt_hongo_estado_salud);
        txt_diagnostico = root.findViewById(R.id.txt_diagnostico);
        txt_sintomas = root.findViewById(R.id.txt_sintomas);
        txt_medidas_prevencion = root.findViewById(R.id.txt_medidas_prevencion);

        img_estado_salud_carouse1 = root.findViewById(R.id.img_estado_salud_carouse1);
        img_estado_salud_carouse2 = root.findViewById(R.id.img_estado_salud_carouse2);
        img_estado_salud_carouse3 = root.findViewById(R.id.img_estado_salud_carouse3);
        return root;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onStart() {
        super.onStart();
        JSONArray jsonArray = cargarJson();
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(claseId);

            txt_titulo_estado_salud.setText(jsonObject.getString("nombre"));
            txt_hongo_estado_salud.setText("Hongo " + jsonObject.getString("hongo"));

            //Cargar imagenes
            switch (claseId) {
                case 0:
                    img_estado_salud_carouse1.setImageResource(R.raw.fitoftora_14);
                    img_estado_salud_carouse2.setImageResource(R.raw.fitoftora_19);
                    img_estado_salud_carouse3.setImageResource(R.raw.fitoftora_41);
                    break;
                case 1:
                    img_estado_salud_carouse1.setImageResource(R.raw.monilia_8);
                    img_estado_salud_carouse2.setImageResource(R.raw.monilia_10);
                    img_estado_salud_carouse3.setImageResource(R.raw.monilia_31);
                    break;
                case 2:
                    img_estado_salud_carouse1.setImageResource(R.raw.sana_7);
                    img_estado_salud_carouse2.setImageResource(R.raw.sana_25);
                    img_estado_salud_carouse3.setImageResource(R.raw.sana_68);
                    break;
            }
            //img_estado_salud_carouse1.setImageResource();
            /*Log.d("ID de mazorcas", "fitf " + R.raw.fitoftora_14);
            Log.d("ID de mazorcas", "fitf " + R.raw.fitoftora_19);
            Log.d("ID de mazorcas", "fitf " + R.raw.fitoftora_41); //1800006

            Log.d("ID de mazorcas", "mon " + R.raw.monilia_8);
            Log.d("ID de mazorcas", "mon " + R.raw.monilia_10);
            Log.d("ID de mazorcas", "mon " + R.raw.monilia_31);

            Log.d("ID de mazorcas", "san " + R.raw.sana_7);
            Log.d("ID de mazorcas", "san " + R.raw.sana_25);
            Log.d("ID de mazorcas", "san " + R.raw.sana_68);*/

            JSONArray jsonArrayDiagnostico = jsonObject.getJSONArray("diagnostico");
            String diagnostico = "";
            for(int i=0; i < jsonArrayDiagnostico.length(); i++) {
                diagnostico = diagnostico + jsonArrayDiagnostico.getString(i);
            }
            txt_diagnostico.setText(diagnostico);

            JSONArray jsonArraySintomas = jsonObject.getJSONArray("sintomas");
            String sintomas = "";
            for(int i=0; i < jsonArraySintomas.length(); i++) {
                sintomas = sintomas + jsonArraySintomas.getString(i);
            }
            txt_sintomas.setText(sintomas);

            JSONArray jsonArrayMedidasPre = jsonObject.getJSONArray("medidas_de_prevencion");
            String medidas_de_prevencion = "";
            for(int i=0; i < jsonArrayMedidasPre.length(); i++) {
                medidas_de_prevencion = medidas_de_prevencion + jsonArrayMedidasPre.getString(i);
            }
            txt_medidas_prevencion.setText(medidas_de_prevencion);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private final View.OnClickListener onClicRegresarAEstadoSalud = view -> {
        Fragment frgEstadoSalud = getActivity().getSupportFragmentManager().findFragmentByTag("frgEstadoSalud");
        regresarAlFragment(this, frgEstadoSalud, true);
    };
    
    private final View.OnClickListener onClicNoEsElResultado = view -> {
        Fragment frgWelcome = getActivity().getSupportFragmentManager().findFragmentByTag("frgWelcome");
        regresarAlFragment(this, frgWelcome, true);
    };

    private void regresarAlFragment(Fragment frgActual, Fragment frgAnterior, boolean eliminarActual) {
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.enter_left_to_rigth,
                    R.anim.exit_left_to_rigth,
                    R.anim.enter_rigth_to_left,
                    R.anim.exit_rigth_to_left);
            transaction.hide(frgActual);
            transaction.show(frgAnterior);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if (eliminarActual) {
                transaction.remove(this);
            }
            transaction.commit();
            try {
                this.finalize();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private JSONArray cargarJson() {
        JSONArray jsonArray = null;
        try {
            InputStream archivo = getResources().openRawResource(R.raw.caco_info);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(archivo));
            StringBuilder stringBuilder = new StringBuilder();

            String jsonString;
            String linea;
            while ( (linea = bufferedReader.readLine()) != null) {
                stringBuilder.append(linea).append("\n");
            }
            jsonString = String.valueOf(stringBuilder);
            archivo.close();
            bufferedReader.close();

            jsonArray = new JSONArray(jsonString);
            //Log.d("Diagnostico", String.valueOf(jsonArray));
        }
        catch (IOException e) {
            Log.e("Diagnostico", "Error al leer el archivo de texto: $e", e);
        } catch (JSONException e) {
            Log.e("Diagnostico", "Error al leer el json: $e", e);
            //throw new RuntimeException(e);
        }

        return jsonArray;
    }
}