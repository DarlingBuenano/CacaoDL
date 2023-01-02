package software.cacaodl.utilidades;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Imagen {
    Bitmap imagenBitmap;
    Context context;

    public Imagen(Context context, Bitmap imagen){
        this.context = context;
        this.imagenBitmap = imagen;
    }

    public void guardarFoto() {
        OutputStream fos = null;
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            System.out.println("Guardando la foto con resolve");
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();

            String nombreFoto = "CacaoDL_" + System.currentTimeMillis();

            values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreFoto);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CacaoDL");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri imageUri = resolver.insert(collection, values);

            try {
                fos = resolver.openOutputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, values, null, null);
        }
        else {
            System.out.println("Guardando la foto tradicionalmente");
            File ruta = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/CacaoDL");
            if (!ruta.exists()) {
                ruta.mkdir();
            }
            String imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/CacaoDL";
            String nombreFoto = "CacaoDL_" + System.currentTimeMillis() + ".jpeg";
            file = new File(imageDir, nombreFoto);
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try{
            boolean guardado = imagenBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (!guardado) {
                Toast.makeText(this.context, "Ocurrió un error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
            // Limpiar el Buffer
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (file != null) { // API menor a la 29
                MediaScannerConnection.scanFile(this.context, new String[]{file.toString()}, null, null);
            }
        } catch (NullPointerException ex) {
            System.out.println(ex);
        }
    }

    public void guardarFotoDeByteABitmap(byte[] bytes) {

    }
}
