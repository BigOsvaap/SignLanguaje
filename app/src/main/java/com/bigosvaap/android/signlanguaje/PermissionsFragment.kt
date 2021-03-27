package com.bigosvaap.android.signlanguaje

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

private const val PERMISSION_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

class PermissionsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!hasPermissions(requireContext())){
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSION_REQUEST_CODE)
        }else{
            navigateToAlphabetRecognition()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE){
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()){
                Toast.makeText(context, getString(R.string.request_granted), Toast.LENGTH_LONG).show()
                navigateToAlphabetRecognition()
            }else{
                Toast.makeText(context, getString(R.string.request_denied), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun navigateToAlphabetRecognition(){
        lifecycleScope.launchWhenStarted {
            val action = PermissionsFragmentDirections.actionPermissionsFragmentToAlphabetRecognitionFragment()
            findNavController().navigate(action)
        }
    }

    companion object{

        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    }

}