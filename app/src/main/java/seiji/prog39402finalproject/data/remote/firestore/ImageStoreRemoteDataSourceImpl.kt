package seiji.prog39402finalproject.data.remote.firestore

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class ImageStoreRemoteDataSourceImpl : ImageStoreRemoteDataSource {
    private val imgRef = Firebase.storage.reference.root.child("/public/capsules/img")

    override suspend fun storeImages(
        imageBytes: List<ByteArray>,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {

        if (imageBytes.isEmpty()) {
            throw IllegalArgumentException("Image list is empty. Why you do this?")
        }

        val savedImageUrls = mutableListOf<String>()
        val uploadTasks = mutableListOf<StorageTask<UploadTask.TaskSnapshot>>()
        val urlGetTasks = mutableListOf<Task<Uri>>()

        imageBytes.forEach { bytes ->
            val newRef = imgRef.child(
                "${bytes.hashCode()}_${System.currentTimeMillis()}.jpg"
            )
            uploadTasks += newRef.putBytes(bytes).addOnCompleteListener {
                if (it.isSuccessful) {
                    urlGetTasks.add(
                        newRef.downloadUrl.addOnSuccessListener { uri ->
                            savedImageUrls += uri.toString()
                        }
                    )
                }
            }
        }

        Tasks.whenAllComplete(uploadTasks)
            .addOnCompleteListener {
                Tasks.whenAllComplete(urlGetTasks).addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess(savedImageUrls)
                    }
                }
            }
    }

    override fun getImages(
        imageLinks: List<String>,
        onImagesLoaded: (List<Bitmap>) -> Unit
    ) {
        val images = mutableListOf<Bitmap>()

        val MAX_SIZE: Long = 1024 * 1024
        val t = imageLinks.map { url ->
            Firebase.storage
                .getReferenceFromUrl(url)
                .getBytes(MAX_SIZE)
                .addOnSuccessListener { res ->
                    val bitmap = BitmapFactory.decodeByteArray(res, 0, res.size)
                    Log.d(TAG, "${bitmap}")
                    images.add(bitmap)
                }
        }

        Tasks.whenAllComplete(t).addOnSuccessListener {
            onImagesLoaded(images)
        }
    }

    companion object {
        const val TAG = "ImageStoreRemoteDataSourceImpl"
    }
}