package seiji.prog39402finalproject.presentation.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import seiji.prog39402finalproject.data.remote.firestore.ImageStoreRemoteDataSource
import seiji.prog39402finalproject.data.remote.firestore.ImageStoreRemoteDataSourceImpl
import java.io.ByteArrayOutputStream

class CameraViewModel() : ViewModel() {

    private val mutImage = MutableLiveData<Bitmap?>(null)
    val image: LiveData<Bitmap?> = mutImage

    fun capture(captured: Bitmap) {
        mutImage.value = captured
    }

    fun reject() {
        mutImage.value = null
    }

}
