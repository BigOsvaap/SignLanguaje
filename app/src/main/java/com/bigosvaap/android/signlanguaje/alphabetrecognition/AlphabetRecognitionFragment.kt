package com.bigosvaap.android.signlanguaje.alphabetrecognition

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bigosvaap.android.signlanguaje.R
import com.bigosvaap.android.signlanguaje.databinding.FragmentAlphabetRecognitionBinding
import com.bigosvaap.android.signlanguaje.ml.StaticSignLanguageModel
import com.bigosvaap.android.signlanguaje.util.YuvToRgbConverter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import java.util.concurrent.Executors

private const val TAG = "AlphabetRecognition"
private const val MAX_RESULT_DISPLAY = 3


class AlphabetRecognitionFragment: Fragment(R.layout.fragment_alphabet_recognition) {

    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var camera: Camera

    private lateinit var previewView: PreviewView

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private val viewModel: AlphabetRecognitionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAlphabetRecognitionBinding.bind(view)

        val adapter = AlphabetRecognitionAdapter()

        with(binding){
            recognitionResults.adapter = adapter
            recognitionResults.animation = null

            previewView = viewFinder
        }

        viewModel.recognitionList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        startCamera()
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable{
            val cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()

            imageAnalysis = ImageAnalysis.Builder()
                // This sets the ideal size for the image to be analyse, CameraX will choose the
                // the most suitable resolution which may not be exactly the same or hold the same
                // aspect ratio
                .setTargetResolution(Size(224, 224))
                // How the Image Analyser should pipe in input, 1. every frame but drop no frame, or
                // 2. go to the latest frame and may drop some frame. The default is 2.
                // STRATEGY_KEEP_ONLY_LATEST. The following line is optional, kept here for clarity
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysisUseCase: ImageAnalysis ->
                    analysisUseCase.setAnalyzer(cameraExecutor, ImageAnalyzer(requireContext()){ predictions ->
                        Log.d(TAG, predictions.toString())
                        viewModel.updateData(predictions)
                    })
                }

            // Select camera, back is the default. If it is not available, choose front camera
            val cameraSelector =
                if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))
                    CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera - try to bind everything at once and CameraX will find
                // the best combination.
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )

                // Attach the preview to preview view, aka View Finder
                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }


        }, ContextCompat.getMainExecutor(requireContext()))
    }



    private class ImageAnalyzer(context: Context, private val listener: (List<Recognition>) -> Unit) :
        ImageAnalysis.Analyzer {


        // Initializing the flowerModel by lazy so that it runs in the same thread when the process
        // method is called.
        private val staticSignLanguageModel: StaticSignLanguageModel by lazy {
            val compatList = CompatibilityList()

            val options = if(compatList.isDelegateSupportedOnThisDevice) {
                Log.d(TAG, "This device is GPU Compatible ")
                Model.Options.Builder().setDevice(Model.Device.GPU).build()
            } else {
                Log.d(TAG, "This device is GPU Incompatible ")
                Model.Options.Builder().setNumThreads(4).build()
            }

            StaticSignLanguageModel.newInstance(context, options)
        }

        override fun analyze(imageProxy: ImageProxy) {

            val items = mutableListOf<Recognition>()

            val tfImage = TensorImage.fromBitmap(toBitmap(imageProxy))

            val outputs = staticSignLanguageModel.process(tfImage)
                .probabilityAsCategoryList.apply {
                    sortByDescending { it.score }
                }.take(MAX_RESULT_DISPLAY)

            for (output in outputs){
                items.add(Recognition(output.label, output.score))
            }

            listener(items.toList())

            imageProxy.close()
        }

        /**
         * Convert Image Proxy to Bitmap
         */
        private val yuvToRgbConverter = YuvToRgbConverter(context)
        private lateinit var bitmapBuffer: Bitmap
        private lateinit var rotationMatrix: Matrix

        @SuppressLint("UnsafeExperimentalUsageError")
        private fun toBitmap(imageProxy: ImageProxy): Bitmap? {

            val image = imageProxy.image ?: return null

            // Initialise Buffer
            if (!::bitmapBuffer.isInitialized) {
                // The image rotation and RGB image buffer are initialized only once
                Log.d(TAG, "Initalise toBitmap()")
                rotationMatrix = Matrix()
                rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                bitmapBuffer = Bitmap.createBitmap(
                    imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
                )
            }

            // Pass image to an image analyser
            yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

            // Create the Bitmap in the correct orientation
            return Bitmap.createBitmap(
                bitmapBuffer,
                0,
                0,
                bitmapBuffer.width,
                bitmapBuffer.height,
                rotationMatrix,
                false
            )
        }

    }

}