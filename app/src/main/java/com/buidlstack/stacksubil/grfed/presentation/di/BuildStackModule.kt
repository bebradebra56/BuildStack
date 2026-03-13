package com.buidlstack.stacksubil.grfed.presentation.di

import com.buidlstack.stacksubil.grfed.data.repo.BuildStackRepository
import com.buidlstack.stacksubil.grfed.data.shar.BuildStackSharedPreference
import com.buidlstack.stacksubil.grfed.data.utils.BuildStackPushToken
import com.buidlstack.stacksubil.grfed.data.utils.BuildStackSystemService
import com.buidlstack.stacksubil.grfed.domain.usecases.BuildStackGetAllUseCase
import com.buidlstack.stacksubil.grfed.presentation.pushhandler.BuildStackPushHandler
import com.buidlstack.stacksubil.grfed.presentation.ui.load.BuildStackLoadViewModel
import com.buidlstack.stacksubil.grfed.presentation.ui.view.BuildStackViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val buildStackModule = module {
    factory {
        BuildStackPushHandler()
    }
    single {
        BuildStackRepository()
    }
    single {
        BuildStackSharedPreference(get())
    }
    factory {
        BuildStackPushToken()
    }
    factory {
        BuildStackSystemService(get())
    }
    factory {
        BuildStackGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        BuildStackViFun(get())
    }
    viewModel {
        BuildStackLoadViewModel(get(), get(), get())
    }
}