package com.buidlstack.stacksubil.grfed.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlstack.stacksubil.grfed.data.shar.BuildStackSharedPreference
import com.buidlstack.stacksubil.grfed.data.utils.BuildStackSystemService
import com.buidlstack.stacksubil.grfed.domain.usecases.BuildStackGetAllUseCase
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackAppsFlyerState
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuildStackLoadViewModel(
    private val buildStackGetAllUseCase: BuildStackGetAllUseCase,
    private val buildStackSharedPreference: BuildStackSharedPreference,
    private val buildStackSystemService: BuildStackSystemService
) : ViewModel() {

    private val _buildStackHomeScreenState: MutableStateFlow<BuildStackHomeScreenState> =
        MutableStateFlow(BuildStackHomeScreenState.BuildStackLoading)
    val buildStackHomeScreenState = _buildStackHomeScreenState.asStateFlow()

    private var buildStackGetApps = false


    init {
        viewModelScope.launch {
            when (buildStackSharedPreference.buildStackAppState) {
                0 -> {
                    if (buildStackSystemService.buildStackIsOnline()) {
                        BuildStackApplication.buildStackConversionFlow.collect {
                            when(it) {
                                BuildStackAppsFlyerState.BuildStackDefault -> {}
                                BuildStackAppsFlyerState.BuildStackError -> {
                                    buildStackSharedPreference.buildStackAppState = 2
                                    _buildStackHomeScreenState.value =
                                        BuildStackHomeScreenState.BuildStackError
                                    buildStackGetApps = true
                                }
                                is BuildStackAppsFlyerState.BuildStackSuccess -> {
                                    if (!buildStackGetApps) {
                                        buildStackGetData(it.buildStackData)
                                        buildStackGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _buildStackHomeScreenState.value =
                            BuildStackHomeScreenState.BuildStackNotInternet
                    }
                }
                1 -> {
                    if (buildStackSystemService.buildStackIsOnline()) {
                        if (BuildStackApplication.BUILD_STACK_FB_LI != null) {
                            _buildStackHomeScreenState.value =
                                BuildStackHomeScreenState.BuildStackSuccess(
                                    BuildStackApplication.BUILD_STACK_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > buildStackSharedPreference.buildStackExpired) {
                            Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Current time more then expired, repeat request")
                            BuildStackApplication.buildStackConversionFlow.collect {
                                when(it) {
                                    BuildStackAppsFlyerState.BuildStackDefault -> {}
                                    BuildStackAppsFlyerState.BuildStackError -> {
                                        _buildStackHomeScreenState.value =
                                            BuildStackHomeScreenState.BuildStackSuccess(
                                                buildStackSharedPreference.buildStackSavedUrl
                                            )
                                        buildStackGetApps = true
                                    }
                                    is BuildStackAppsFlyerState.BuildStackSuccess -> {
                                        if (!buildStackGetApps) {
                                            buildStackGetData(it.buildStackData)
                                            buildStackGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Current time less then expired, use saved url")
                            _buildStackHomeScreenState.value =
                                BuildStackHomeScreenState.BuildStackSuccess(
                                    buildStackSharedPreference.buildStackSavedUrl
                                )
                        }
                    } else {
                        _buildStackHomeScreenState.value =
                            BuildStackHomeScreenState.BuildStackNotInternet
                    }
                }
                2 -> {
                    _buildStackHomeScreenState.value =
                        BuildStackHomeScreenState.BuildStackError
                }
            }
        }
    }


    private suspend fun buildStackGetData(conversation: MutableMap<String, Any>?) {
        val buildStackData = buildStackGetAllUseCase.invoke(conversation)
        if (buildStackSharedPreference.buildStackAppState == 0) {
            if (buildStackData == null) {
                buildStackSharedPreference.buildStackAppState = 2
                _buildStackHomeScreenState.value =
                    BuildStackHomeScreenState.BuildStackError
            } else {
                buildStackSharedPreference.buildStackAppState = 1
                buildStackSharedPreference.apply {
                    buildStackExpired = buildStackData.buildStackExpires
                    buildStackSavedUrl = buildStackData.buildStackUrl
                }
                _buildStackHomeScreenState.value =
                    BuildStackHomeScreenState.BuildStackSuccess(buildStackData.buildStackUrl)
            }
        } else  {
            if (buildStackData == null) {
                _buildStackHomeScreenState.value =
                    BuildStackHomeScreenState.BuildStackSuccess(buildStackSharedPreference.buildStackSavedUrl)
            } else {
                buildStackSharedPreference.apply {
                    buildStackExpired = buildStackData.buildStackExpires
                    buildStackSavedUrl = buildStackData.buildStackUrl
                }
                _buildStackHomeScreenState.value =
                    BuildStackHomeScreenState.BuildStackSuccess(buildStackData.buildStackUrl)
            }
        }
    }


    sealed class BuildStackHomeScreenState {
        data object BuildStackLoading : BuildStackHomeScreenState()
        data object BuildStackError : BuildStackHomeScreenState()
        data class BuildStackSuccess(val data: String) : BuildStackHomeScreenState()
        data object BuildStackNotInternet: BuildStackHomeScreenState()
    }
}