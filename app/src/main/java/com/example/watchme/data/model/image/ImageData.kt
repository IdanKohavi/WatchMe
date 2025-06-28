package com.example.watchme.data.model.image

import com.google.gson.annotations.SerializedName

data class ImageData(
    @SerializedName("file_path") val filePath: String
)
