package seiji.prog39402finalproject.data.remote

import android.graphics.Bitmap
import android.net.Uri

interface ImageStoreRemoteDataSource {
    suspend fun storeImages(
        imageBytes: List<ByteArray>,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    )

    fun getImages(
        imageLinks: List<String>,
        onImagesLoaded: (List<Bitmap>) -> Unit
    )
}