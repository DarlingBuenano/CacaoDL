package software.cacaodl.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import software.cacaodl.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CapturarImagen : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var root: View

    private var imageCapture: ImageCapture? = null
    private var previewView: PreviewView? = null
    private var outputDirectory: File? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_capturar_imagen, container, false)

        // Check camera permissions if all permission granted
        // start camera else ask for the permission
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(context as Activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // set on click listener for the button of capture photo
        // it calls a method which is implemented below
        root.findViewById<Button>(R.id.capturarImagen).setOnClickListener { takePhoto() }
        previewView = root.findViewById(R.id.previewCamera)
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        return root
    }

    private fun takePhoto(){
        val imageCapture = imageCapture ?: return

        val photoFile = File(
                outputDirectory, "CacaoDL_" +
                SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"

        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        context?.let { ContextCompat.getMainExecutor(it) }?.let {
            imageCapture.takePicture(
                outputOptions,
                    it,
                object: ImageCapture.OnImageSavedCallback {
                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Error al capturar la foto: ${exception.message}", exception)
                    }
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)

                        val frgEstadoSalud = EstadoSalud(savedUri)
                        abrirFragment(this@CapturarImagen, frgEstadoSalud, "frgEstadoSalud")

                        val msg = "Foto capturada correctamente: $savedUri"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)
                    }
                }
        )
        }
    }

    private fun startCamera(){
        val cameraProviderFuture = context?.let { ProcessCameraProvider.getInstance(it) }

        cameraProviderFuture?.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView?.surfaceProvider)
                    }

            imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, context?.let { ContextCompat.getMainExecutor(it) })
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 -> ContextCompat.checkSelfPermission(it1, it) } == PackageManager.PERMISSION_GRANTED
    }

    // creates a folder inside internal storage
    private fun getOutputDirectory(): File? {
        val mediaDir = context?.applicationContext?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdir() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else context?.filesDir
    }

    // checks the camera permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(context, "Permisos no concedidos por el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "CapturarFoto"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CapturarImagen().apply {
                    arguments = Bundle().apply {}
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun abrirFragment(frgActual: Fragment, frgPosterior: Fragment, tagFrgPosterior: String) {
        cameraExecutor.shutdown()
        val transaction: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(
                        R.anim.enter_rigth_to_left,
                        R.anim.exit_rigth_to_left,
                        R.anim.enter_left_to_rigth,
                        R.anim.exit_left_to_rigth)
        transaction?.hide(frgActual)
        if (frgPosterior.isAdded) {
            transaction?.show(frgPosterior)
        } else {
            transaction?.add(R.id.frg_container_view, frgPosterior, tagFrgPosterior)
        }
        //transaction?.remove(frgActual)
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.commit()
    }
}