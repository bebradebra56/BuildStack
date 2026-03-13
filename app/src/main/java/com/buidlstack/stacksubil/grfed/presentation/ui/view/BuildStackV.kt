package com.buidlstack.stacksubil.grfed.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import com.buidlstack.stacksubil.grfed.presentation.ui.load.BuildStackLoadFragment
import org.koin.android.ext.android.inject

class BuildStackV : Fragment(){

    private lateinit var buildStackPhoto: Uri
    private var buildStackFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val buildStackTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        buildStackFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        buildStackFilePathFromChrome = null
    }

    private val buildStackTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            buildStackFilePathFromChrome?.onReceiveValue(arrayOf(buildStackPhoto))
            buildStackFilePathFromChrome = null
        } else {
            buildStackFilePathFromChrome?.onReceiveValue(null)
            buildStackFilePathFromChrome = null
        }
    }

    private val buildStackDataStore by activityViewModels<BuildStackDataStore>()


    private val buildStackViFun by inject<BuildStackViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (buildStackDataStore.buildStackView.canGoBack()) {
                        buildStackDataStore.buildStackView.goBack()
                        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "WebView can go back")
                    } else if (buildStackDataStore.buildStackViList.size > 1) {
                        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "WebView can`t go back")
                        buildStackDataStore.buildStackViList.removeAt(buildStackDataStore.buildStackViList.lastIndex)
                        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "WebView list size ${buildStackDataStore.buildStackViList.size}")
                        buildStackDataStore.buildStackView.destroy()
                        val previousWebView = buildStackDataStore.buildStackViList.last()
                        buildStackAttachWebViewToContainer(previousWebView)
                        buildStackDataStore.buildStackView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (buildStackDataStore.buildStackIsFirstCreate) {
            buildStackDataStore.buildStackIsFirstCreate = false
            buildStackDataStore.buildStackContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return buildStackDataStore.buildStackContainerView
        } else {
            return buildStackDataStore.buildStackContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "onViewCreated")
        if (buildStackDataStore.buildStackViList.isEmpty()) {
            buildStackDataStore.buildStackView = BuildStackVi(requireContext(), object :
                BuildStackCallBack {
                override fun buildStackHandleCreateWebWindowRequest(buildStackVi: BuildStackVi) {
                    buildStackDataStore.buildStackViList.add(buildStackVi)
                    Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "WebView list size = ${buildStackDataStore.buildStackViList.size}")
                    Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "CreateWebWindowRequest")
                    buildStackDataStore.buildStackView = buildStackVi
                    buildStackVi.buildStackSetFileChooserHandler { callback ->
                        buildStackHandleFileChooser(callback)
                    }
                    buildStackAttachWebViewToContainer(buildStackVi)
                }

            }, buildStackWindow = requireActivity().window).apply {
                buildStackSetFileChooserHandler { callback ->
                    buildStackHandleFileChooser(callback)
                }
            }
            buildStackDataStore.buildStackView.buildStackFLoad(arguments?.getString(
                BuildStackLoadFragment.BUILD_STACK_D) ?: "")
//            ejvview.fLoad("www.google.com")
            buildStackDataStore.buildStackViList.add(buildStackDataStore.buildStackView)
            buildStackAttachWebViewToContainer(buildStackDataStore.buildStackView)
        } else {
            buildStackDataStore.buildStackViList.forEach { webView ->
                webView.buildStackSetFileChooserHandler { callback ->
                    buildStackHandleFileChooser(callback)
                }
            }
            buildStackDataStore.buildStackView = buildStackDataStore.buildStackViList.last()

            buildStackAttachWebViewToContainer(buildStackDataStore.buildStackView)
        }
        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "WebView list size = ${buildStackDataStore.buildStackViList.size}")
    }

    private fun buildStackHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        buildStackFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Launching file picker")
                    buildStackTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Launching camera")
                    buildStackPhoto = buildStackViFun.buildStackSavePhoto()
                    buildStackTakePhoto.launch(buildStackPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                buildStackFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun buildStackAttachWebViewToContainer(w: BuildStackVi) {
        buildStackDataStore.buildStackContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            buildStackDataStore.buildStackContainerView.removeAllViews()
            buildStackDataStore.buildStackContainerView.addView(w)
        }
    }


}