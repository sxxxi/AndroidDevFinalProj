package seiji.prog39402finalproject.domain.forms

import android.graphics.Bitmap
import seiji.prog39402finalproject.domain.models.Capsule

data class CapsuleCreateForm(
    val newCapsule: Capsule,
    val images: List<Bitmap>
)
