package app.cash.paparazzi.sample

import android.graphics.BitmapFactory
import androidx.appcompat.widget.AppCompatImageView
import app.cash.paparazzi.sample.AppMessageImage.Fill
import app.cash.paparazzi.sample.AppMessageImage.Fixed
import app.cash.paparazzi.sample.AppMessageImage.Inset

class FakeImageLoader : AppMessageImageLoader {
  override fun load(
    image: AppMessageImage?,
    imageView: AppCompatImageView
  ) {
    val imageResource = when (image) {
      is Inset -> "glowcard-inset.png"
      is Fill -> "glowcard-fill.png"
      is Fixed -> "glowcard-fixed.png"
      null -> null
    }

    if (imageResource != null) {
      val resourceAsStream = javaClass.classLoader!!.getResourceAsStream(imageResource)
      val bitmap = BitmapFactory.decodeStream(resourceAsStream)
      imageView.setImageBitmap(bitmap)
    }
  }
}