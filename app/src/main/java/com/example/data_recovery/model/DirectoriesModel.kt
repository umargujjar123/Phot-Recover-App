package com.example.data_recovery.model

import java.io.Serializable

data class DirectoriesModel(
    val name: String?,
    val image: String?,
    var isSelected: Boolean = false
):Serializable

data class DirList(var directories: MutableList<DirectoriesModel>):Serializable