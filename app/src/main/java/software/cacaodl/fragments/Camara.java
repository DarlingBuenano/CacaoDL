package software.cacaodl.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String camaraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSession;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private File file;
    private File folder;
    private String foldername = "/Pictures/CacaoDL";
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

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
        if (textureCamara != null) {
            textureCamara.setSurfaceTextureListener(textureListener);
        }
        btnOpturador = root.findViewById(R.id.btn_opturador);
        btnOpturador.setOnClickListener(onClicOpturador);
        return root;
    }

    private View.OnClickListener onClicOpturador = view -> {
        takePicture();
    };

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
            cameraDevice.close();
            cameraDevice = null;
            System.out.println(i);
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    protected void takePicture() {
        if (cameraDevice == null){
            Log.e(TAG, "cameraDevice es null");
            return;
        }
        if (!isExternalStorageAvailableForRW() || isExternalStorageReadOnly()) {
            btnOpturador.setEnabled(false);
        }
        if (isStoragePermissionGranted()) {
            CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
                Size[] jpegSizes = null;
                if (characteristics != null) {
                    jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                }
                /*int width = 300;
                int height = 300;*/
                if (jpegSizes != null && jpegSizes.length > 0) {
                    //width = jpegSizes[0].getWidth();
                    //height = jpegSizes[0].getHeight();
                    System.out.println("Caracteristica JPEG Width de la imagen: " + jpegSizes[0].getWidth());
                    System.out.println("Caracteristica JPEG Height de la imagen: " + jpegSizes[0].getHeight());
                }
                ImageReader reader = ImageReader.newInstance(SIZE_IMAGE_YOLOV7, SIZE_IMAGE_YOLOV7, ImageFormat.JPEG, 1);
                List<Surface> outputSurfaces = new ArrayList<Surface>(2);
                outputSurfaces.add(reader.getSurface());
                outputSurfaces.add(new Surface(textureCamara.getSurfaceTexture()));

                final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                //Orientation
                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

                file = null;
                folder = new File(foldername);
                String nombreFoto = "CacaoDL_" + System.currentTimeMillis() + ".jpeg";
                file = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/CacaoDL", "/" + nombreFoto);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader imageReader) {
                        Image image = null;
                        try {
                            image = imageReader.acquireLatestImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.capacity()];
                            buffer.get(bytes);
                            imagenBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            save(bytes);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (image != null) {
                                image.close();
                            }
                        }
                    }
                    private void save(byte[] bytes) throws IOException {
                        OutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);
                        } finally {
                            if (outputStream != null) {
                                outputStream.flush();
                                outputStream.close();
                            }
                        }
                    }
                };
                reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
                final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        System.out.println("Imagen guardada");
                        Log.d(TAG, "Imagen guardada: " + file);
                        //createCameraPreview();
                        if (imagenBitmap != null)
                            abrirEstadoSalud();
                        else
                            System.out.println("imagenBitmap es null");
                    }
                };
                cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        try {
                            cameraCaptureSession.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    }
                }, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirEstadoSalud() {
        Fragment frgEstadoSalud = getActivity().getSupportFragmentManager().findFragmentByTag("frgEstadoSalud");
        frgEstadoSalud = new EstadoSalud(imagenBitmap);
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.enter_left_to_rigth,
                    R.anim.exit_left_to_rigth,
                    R.anim.enter_rigth_to_left,
                    R.anim.exit_rigth_to_left);
            transaction.hide(this);
            System.out.println("ocultando este fragment");
            System.out.println("esta agregado: " + frgEstadoSalud.isAdded());
            if (frgEstadoSalud.isAdded() == false){
                transaction.add(R.id.frg_container_view, frgEstadoSalud, "frgEstadoSalud");
            }
            System.out.println("esta oculto: " + frgEstadoSalud.isHidden());
            if (frgEstadoSalud.isHidden() == false) {
                transaction.show(frgEstadoSalud);
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.remove(this);
            transaction.commit();
            /*try {
                this.finalize();
            } catch (Throwable e) {
                e.printStackTrace();
            }*/
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState))
            return true;
        return false;
    }
    private boolean isExternalStorageAvailableForRW() {
        String extStorageState = Environment.getExternalStorageState();
        if (extStorageState.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        return false;
    }

    protected  void createCameraPreview() {
        try {
            SurfaceTexture texture = textureCamara.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCapSession) {
                    if (cameraDevice == null)
                        return;
                    cameraCaptureSession = cameraCapSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    System.out.println("Configuracion cambiada");
                    Log.d(TAG, "Configuracion cambiada");
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            camaraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camaraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(camaraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "OpenCamera X");
    }

    protected void updatePreview() {
        if (cameraDevice == null) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getContext(), "Lo siento, no diste acceso a la cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        System.out.println("onStart");
        startBackgroundThread();
        System.out.println(textureCamara.isAvailable());
        if (!textureCamara.isAvailable()) {
            textureCamara.setSurfaceTextureListener(textureListener);
        }
        openCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        stopBackgroundThread();
    }
}