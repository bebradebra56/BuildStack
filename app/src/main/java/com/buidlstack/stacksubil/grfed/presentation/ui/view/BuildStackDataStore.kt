package com.buidlstack.stacksubil.grfed.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class BuildStackDataStore : ViewModel(){
    val buildStackViList: MutableList<BuildStackVi> = mutableListOf()
    var buildStackIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var buildStackContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var buildStackView: BuildStackVi

}