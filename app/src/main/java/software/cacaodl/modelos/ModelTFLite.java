package software.cacaodl.modelos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Display;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

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
import java.util.Map;

import software.cacaodl.ml.ModelCacaodl;
import software.cacaodl.ml.ModelFlower;

public class ModelTFLite {
    private Context contexto;
    private Bitmap imagen;
    private Interpreter interpreter;
    private static final int efficientnet_lite4 = 300;
    private JSONArray jsonArrayProbabilidades;

    public ModelTFLite(Context context, Bitmap img) {
        this.contexto = context;
        this.imagen = img;
        this.jsonArrayProbabilidades = new JSONArray();
    }

    public JSONArray inferencia() {
        this.imagen = Bitmap.createScaledBitmap(this.imagen, efficientnet_lite4, efficientnet_lite4, true);

        try{
            ModelCacaodl modelo = ModelCacaodl.newInstance(this.contexto);

            // Crea las referencias de entrada
            TensorImage tensorImage = TensorImage.fromBitmap(this.imagen);

            // Ejecuta la inferencia del modelo y obtenga los resultados
            ModelCacaodl.Outputs outputs = modelo.process(tensorImage);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            System.out.println("Categorias:");
            for (int x = 0; x < probability.size(); x++) {
                System.out.println(x + ": ");
                System.out.println("    Label: " + probability.get(x).getLabel());
                System.out.println("    Score: " + probability.get(x).getScore());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nombre", probability.get(x).getLabel());
                jsonObject.put("porcentaje", probability.get(x).getScore());
                this.jsonArrayProbabilidades.put(jsonObject);
            }
            // Libera los recursos del modelo si ya no se usan.
            modelo.close();
        }
        catch (IOException | JSONException e) {
            System.out.println("Ocurrió un error durante la inferencia del modelo");
            System.out.println(e);
        }
        return jsonArrayProbabilidades;
    }

    public void descargarModeloDeFirebase() {

        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("cacao-dl", DownloadType.LOCAL_MODEL, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        System.out.println("Modelo descargado con éxito");
                        System.out.println(model.getName());
                        System.out.println(model.getSize());
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        }
                    }
                });
    }

    public void clasificarImagenConFirebase() {
        ByteBuffer input = getImageAsByteBuffer(this.imagen);
        int bufferSize = 4 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
        ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
        interpreter.run(input, modelOutput);

        modelOutput.rewind();
        FloatBuffer probabilities = modelOutput.asFloatBuffer();

        System.out.println("Categorias:");
        for (int x = 0; x < probabilities.capacity(); x++) {
            System.out.println(x + ":");
            System.out.println("    Label: " + probabilities.toString());
            System.out.println("    Score: " + probabilities.get(x));
        }
    }

    public ByteBuffer getImageAsByteBuffer(Bitmap image){
        ImageProcessor imageProcessor;
        TensorImage xceptionTfliteInput;
        imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(380, 380, ResizeOp.ResizeMethod.BILINEAR))
                        .add(new NormalizeOp(0, 255))
                        .build();
        xceptionTfliteInput = new TensorImage(DataType.FLOAT32);

        xceptionTfliteInput.load(image);
        xceptionTfliteInput = imageProcessor.process(xceptionTfliteInput);
        return  xceptionTfliteInput.getBuffer();
    }

    public int getIndexMaxScore(FloatBuffer probabilities){
        float MaxScore=0; int pos=0;
        for (int i = 0; i < probabilities.capacity(); i++) {
            if(probabilities.get(i) >= MaxScore) {
                pos = i;
                MaxScore=probabilities.get(i);
            }
        }
        return pos;
    }
}