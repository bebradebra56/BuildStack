package com.buidlstack.stacksubil.grfed.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.buidlstack.stacksubil.MainActivity
import com.buidlstack.stacksubil.R
import com.buidlstack.stacksubil.databinding.FragmentLoadBuildStackBinding
import com.buidlstack.stacksubil.grfed.data.shar.BuildStackSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BuildStackLoadFragment : Fragment(R.layout.fragment_load_build_stack) {
    private lateinit var buildStackLoadBinding: FragmentLoadBuildStackBinding

    private val buildStackLoadViewModel by viewModel<BuildStackLoadViewModel>()

    private val buildStackSharedPreference by inject<BuildStackSharedPreference>()

    private var buildStackUrl = ""

    private val buildStackRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        buildStackSharedPreference.buildStackNotificationState = 2
        buildStackNavigateToSuccess(buildStackUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildStackLoadBinding = FragmentLoadBuildStackBinding.bind(view)

        buildStackLoadBinding.buildStackGrandButton.setOnClickListener {
            val buildStackPermission = Manifest.permission.POST_NOTIFICATIONS
            buildStackRequestNotificationPermission.launch(buildStackPermission)
        }

        buildStackLoadBinding.buildStackSkipButton.setOnClickListener {
            buildStackSharedPreference.buildStackNotificationState = 1
            buildStackSharedPreference.buildStackNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            buildStackNavigateToSuccess(buildStackUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                buildStackLoadViewModel.buildStackHomeScreenState.collect {
                    when (it) {
                        is BuildStackLoadViewModel.BuildStackHomeScreenState.BuildStackLoading -> {

                        }

                        is BuildStackLoadViewModel.BuildStackHomeScreenState.BuildStackError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is BuildStackLoadViewModel.BuildStackHomeScreenState.BuildStackSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val buildStackNotificationState = buildStackSharedPreference.buildStackNotificationState
                                when (buildStackNotificationState) {
                                    0 -> {
                                        buildStackLoadBinding.buildStackNotiGroup.visibility = View.VISIBLE
                                        buildStackLoadBinding.buildStackLoadingGroup.visibility = View.GONE
                                        buildStackUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > buildStackSharedPreference.buildStackNotificationRequest) {
                                            buildStackLoadBinding.buildStackNotiGroup.visibility = View.VISIBLE
                                            buildStackLoadBinding.buildStackLoadingGroup.visibility = View.GONE
                                            buildStackUrl = it.data
                                        } else {
                                            buildStackNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        buildStackNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                buildStackNavigateToSuccess(it.data)
                            }
                        }

                        BuildStackLoadViewModel.BuildStackHomeScreenState.BuildStackNotInternet -> {
                            buildStackLoadBinding.buildStackStateGroup.visibility = View.VISIBLE
                            buildStackLoadBinding.buildStackLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun buildStackNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_buildStackLoadFragment_to_buildStackV,
            bundleOf(BUILD_STACK_D to data)
        )
    }

    companion object {
        const val BUILD_STACK_D = "buildStackData"
    }
}