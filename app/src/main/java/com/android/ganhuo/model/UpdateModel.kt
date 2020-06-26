package com.android.ganhuo.model

data class UpdateModel(
    val binary: Binary,
    val build: String,
    val changelog: Any,
    val direct_install_url: String,
    val installUrl: String,
    val install_url: String,
    val name: String,
    val update_url: String,
    val updated_at: Int,
    val version: String,
    val versionShort: String
)