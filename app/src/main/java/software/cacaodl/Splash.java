package software.cacaodl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(() -> {
            boolean sesion = pref.getBoolean("sesion", false);
            Intent intent;
            if (sesion) {
                // Ir directo a la pantalla principal
                intent = new Intent(getApplicationContext(), MainActivity.class);
            } else {
                // Ir a la guia de bienvenida
                intent = new Intent(getApplicationContext(), GuiaBienvenida.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}