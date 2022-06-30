package com.example.data_recovery.model

import java.io.Serializable

data class DirectoriesModel(
    val name: String?,
    val image: String?
):Serializable

data class DirList(val directories: List<DirectoriesModel>):Serializable