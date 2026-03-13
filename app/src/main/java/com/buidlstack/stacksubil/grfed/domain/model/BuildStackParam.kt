package com.buidlstack.stacksubil.grfed.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


private const val BUILD_STACK_A = "com.buidlstack.stacksubil"
private const val BUILD_STACK_B = "buildstack-bf460"
@Serializable
data class BuildStackParam (
    @SerialName("af_id")
    val buildStackAfId: String,
    @SerialName("bundle_id")
    val buildStackBundleId: String = BUILD_STACK_A,
    @SerialName("os")
    val buildStackOs: String = "Android",
    @SerialName("store_id")
    val buildStackStoreId: String = BUILD_STACK_A,
    @SerialName("locale")
    val buildStackLocale: String,
    @SerialName("push_token")
    val buildStackPushToken: String,
    @SerialName("firebase_project_id")
    val buildStackFirebaseProjectId: String = BUILD_STACK_B,
    )