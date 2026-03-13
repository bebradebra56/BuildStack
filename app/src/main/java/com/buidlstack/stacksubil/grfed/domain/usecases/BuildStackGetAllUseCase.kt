package com.buidlstack.stacksubil.grfed.domain.usecases

import android.util.Log
import com.buidlstack.stacksubil.grfed.data.repo.BuildStackRepository
import com.buidlstack.stacksubil.grfed.data.utils.BuildStackPushToken
import com.buidlstack.stacksubil.grfed.data.utils.BuildStackSystemService
import com.buidlstack.stacksubil.grfed.domain.model.BuildStackEntity
import com.buidlstack.stacksubil.grfed.domain.model.BuildStackParam
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication

class BuildStackGetAllUseCase(
    private val buildStackRepository: BuildStackRepository,
    private val buildStackSystemService: BuildStackSystemService,
    private val buildStackPushToken: BuildStackPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : BuildStackEntity?{
        val params = BuildStackParam(
            buildStackLocale = buildStackSystemService.buildStackGetLocale(),
            buildStackPushToken = buildStackPushToken.buildStackGetToken(),
            buildStackAfId = buildStackSystemService.buildStackGetAppsflyerId()
        )
        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Params for request: $params")
        return buildStackRepository.buildStackGetClient(params, conversion)
    }



}