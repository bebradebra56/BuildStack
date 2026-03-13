package com.buidlstack.stacksubil.grfed.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BuildStackEntity (
    @SerialName("ok")
    val buildStackOk: Boolean,
    @SerialName("url")
    val buildStackUrl: String,
    @SerialName("expires")
    val buildStackExpires: Long,
)