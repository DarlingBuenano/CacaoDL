package software.cacaodl.modelos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.Category;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import software.cacaodl.ml.ModelCacaodl;
import software.cacaodl.ml.ModelCacaodl2;

public class ModelTFLite {
    private Context contexto;
    private Bitmap imagen;
    private final static int tamaño_imagen = 640;
    private final static int max_resultado = 6;
    private JSONArray jsonArrayProbabilidades;

    public ModelTFLite(Context context, Bitmap img) {
        this.contexto = context;
        this.imagen = img.copy(Bitmap.Config.ARGB_8888, true);
    }

    public JSONArray inferencia_objectdetection() {
        //this.imagen = Bitmap.createScaledBitmap(this.imagen, tamaño_imagen, tamaño_imagen, true);
        this.jsonArrayProbabilidades = new JSONArray();
        try {
            ModelCacaodl2 model = ModelCacaodl2.newInstance(this.contexto);

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(this.imagen);

            // Runs model inference and gets result.
            ModelCacaodl2.Outputs outputs = model.process(image);
            List<ModelCacaodl2.DetectionResult> detectionResultList = outputs.getDetectionResultList();

            int cant_resultados = detectionResultList.size();
            if (cant_resultados > max_resultado)
                cant_resultados = max_resultado;

            for (int i = 0; i < cant_resultados; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("objeto", i);
                jsonObject.put("categoria", detectionResultList.get(i).getCategoryAsString());
                jsonObject.put("confianza", detectionResultList.get(i).getScoreAsFloat());

                RectF rectangulo = detectionResultList.get(i).getLocationAsRectF();
                jsonObject.put("left", rectangulo.left);
                jsonObject.put("top", rectangulo.top);
                jsonObject.put("right", rectangulo.right);
                jsonObject.put("bottom", rectangulo.bottom);

                //System.out.println(jsonObject);
                this.jsonArrayProbabilidades.put(jsonObject);
            }
            // Releases model resources if no longer used.
            model.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.jsonArrayProbabilidades;
    }
}