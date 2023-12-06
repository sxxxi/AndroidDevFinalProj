package seiji.prog39402finalproject.data.repository.capsule

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import seiji.prog39402finalproject.data.mappers.CapsuleMapper
import seiji.prog39402finalproject.data.remote.capsule.CapsuleRemoteDataSource
import seiji.prog39402finalproject.data.remote.capsule.CapsuleRemoteDataSourceImpl
import seiji.prog39402finalproject.data.remote.capsule.ImageStoreRemoteDataSource
import seiji.prog39402finalproject.data.remote.capsule.ImageStoreRemoteDataSourceImpl
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.domain.forms.CapsuleCreateForm
import java.io.ByteArrayOutputStream

class CapsuleFirestoreRepositoryImpl(
    private val imgStore: ImageStoreRemoteDataSource = ImageStoreRemoteDataSourceImpl(),
    private val capsuleStore: CapsuleRemoteDataSource = CapsuleRemoteDataSourceImpl(),
    private val capsuleMapper: CapsuleMapper = CapsuleMapper()
) : CapsuleFirestoreRepository {

    override suspend fun getNearbyCapsules(
        center: LatLng,
        radiusM: Double,
        onSuccess: (List<CapsuleRemoteModel>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {

        capsuleStore.getNearbyCapsules(
            center = center,
            radiusM = radiusM,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    override suspend fun dropCapsule(
        capsuleForm: CapsuleCreateForm,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) = withContext(Dispatchers.IO) {
        imgStore.storeImages(
            imageBytes = capsuleForm.images.map {
                val out = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 50, out)
                out.toByteArray()
            },
            onSuccess = { imageLinks ->
                val updatedCapsule = capsuleForm.newCapsule.copy(
                    images = imageLinks
                )

                capsuleStore.createCapsule(
                    capsule = capsuleMapper.toRemote(updatedCapsule),
                    onSuccess = {
                        onSuccess()
                        Log.d(TAG, "Capsule create success")
                    },
                    onFailure = onFailure
                )
            },
        )
    }

    override fun getImagesFromLinks(
        imageLinks: List<String>,
        onImagesLoaded: (List<Bitmap>) -> Unit
    ) {
        imgStore.getImages(imageLinks, onImagesLoaded)
    }

    companion object {
        const val TAG = "CapsuleFirestoreRepositoryImpl"
    }
}