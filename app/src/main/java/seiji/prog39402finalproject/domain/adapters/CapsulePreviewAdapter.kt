package seiji.prog39402finalproject.domain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import seiji.prog39402finalproject.databinding.CapsuleLayoutBinding
import seiji.prog39402finalproject.domain.models.Capsule

class CapsulePreviewAdapter(
    private val capsules: List<Capsule>,
    private val onClick: (Capsule) -> Unit = {}
) : RecyclerView.Adapter<CapsulePreviewAdapter.Holder>() {

    inner class Holder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            CapsuleLayoutBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
        )
    }

    override fun getItemCount(): Int {
        return capsules.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val capsule = capsules[position]
        CapsuleLayoutBinding.bind(holder.itemView).apply {
            textPreviewTitle.text = capsule.title
            textPreviewBody.text = capsule.body
            root.setOnClickListener {
                onClick(capsule)
            }
        }
    }
}