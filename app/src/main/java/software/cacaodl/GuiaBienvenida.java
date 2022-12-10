package software.cacaodl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import software.cacaodl.adaptadores.ViewPageAdapter;

public class GuiaBienvenida extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia_bienvenida);

        ArrayList<Integer> imagenesArrayId = new ArrayList<>();
        ArrayList<String> txtTitulos = new ArrayList<>();
        ArrayList<String> txtDescripciones = new ArrayList<>();

        imagenesArrayId.add(R.drawable.vector_guia_pag01);
        imagenesArrayId.add(R.drawable.vector_guia_pag02);

        txtTitulos.add(getString(R.string.guia_pag1_titulo));
        txtTitulos.add(getString(R.string.guia_pag2_titulo));

        txtDescripciones.add(getString(R.string.guia_pag1_descripcion));
        txtDescripciones.add(getString(R.string.guia_pag2_descripcion));

        ViewPager2 vw_guia = findViewById(R.id.vw_guia_bienvenida);
        vw_guia.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        vw_guia.setAdapter(new ViewPageAdapter(imagenesArrayId, txtTitulos, txtDescripciones));
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*Button btn_terminar_guia = findViewById(R.id.btn_terminar_guia);
        btn_terminar_guia.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });*/
    }

    public void clicTerminarGuiaDeBienvenida(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}