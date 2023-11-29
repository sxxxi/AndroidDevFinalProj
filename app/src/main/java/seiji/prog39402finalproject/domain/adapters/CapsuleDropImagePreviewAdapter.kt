package seiji.prog39402finalproject.domain.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import seiji.prog39402finalproject.databinding.ImagePagerLayoutBinding

class CapsuleDropImagePreviewAdapter(
    private val images: List<Bitmap>,
    private val onImageClick: (Bitmap) -> Unit
) : RecyclerView.Adapter<CapsuleDropImagePreviewAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ImagePagerLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {


            }.root
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        ImagePagerLayoutBinding.bind(holder.itemView).apply {
            val currentBitmap = images[position]
            subjectImage.setOnClickListener {
                onImageClick(currentBitmap)
            }
            subjectImage.setImageBitmap(currentBitmap)
        }.root
    }


}