package software.cacaodl.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;

import software.cacaodl.R;

public class Camara extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "AndroidCamera2API";
    private static final int SIZE_IMAGE_YOLOV7 = 640;
    private Button btnOpturador;
    private TextureView textureCamara;
    private Bitmap imagenBitmap;
    private FragmentManager manager;

    private Size imageDimension;
    private File file;
    private File folder;
    private String foldername = "/Pictures/CacaoDL";
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    public Camara() {
        // Required empty public constructor
    }

    public static Camara newInstance(String param1, String param2) {
        Camara fragment = new Camara();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_camara, container, false);
        textureCamara = root.findViewById(R.id.texture_camara);
        btnOpturador = root.findViewById(R.id.btn_opturador);
        btnOpturador.setOnClickListener(onClicOpturador);
        return root;
    }

    private View.OnClickListener onClicOpturador = view -> {
        //takePicture();
    };
}