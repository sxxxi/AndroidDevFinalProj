package seiji.prog39402finalproject.domain.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import seiji.prog39402finalproject.databinding.ImagePagerLayoutBinding
import java.io.File

class ImagePagerAdapter(
    private val bitmaps: List<Bitmap>
) : PagerAdapter() {
    override fun getCount(): Int {
        return bitmaps.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return ImagePagerLayoutBinding.inflate(LayoutInflater
            .from(container.context), container, false)
            .apply {
                subjectImage.setImageBitmap(bitmaps[position])
            }.root.also {
                (container as ViewPager).addView(it)
            }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as ConstraintLayout)
    }
}