// Copyright Square, Inc.
package app.cash.paparazzi.sample

import androidx.appcompat.widget.AppCompatImageView

interface AppMessageImageLoader {
  fun load(
    image: AppMessageImage?,
    imageView: AppCompatImageView
  )
}
