package software.cacaodl.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import software.cacaodl.R;

public class Diagnostico extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private ImageButton btnRegresarAEstadoSalud;
    private TextView btnNoEsElResultado;
    private int claseId;

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
        return root;
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
}