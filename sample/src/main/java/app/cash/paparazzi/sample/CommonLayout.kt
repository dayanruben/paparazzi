// Copyright Square, Inc.
package app.cash.paparazzi.sample

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import app.cash.paparazzi.sample.AppMessageImage.Fill
import app.cash.paparazzi.sample.AppMessageImage.Fixed
import app.cash.paparazzi.sample.AppMessageImage.Inset
import com.squareup.contour.ContourLayout

/** This file contains layout logic that, at time of writing, is common to both App Message placements:
 *  - [InlineAppMessageView]
 *  - [PopupAppMessageView]
 *
 *  If this layout logic diverges, it's fine to just move it from here to the respective
 *  placement-specific implementations
 */

internal fun ContourLayout.layoutImage(image: AppCompatImageView) {
  image.layoutBy(
    leftTo { parent.left() }
      .rightTo { parent.right() },
    emptyY() // Computed at model-setting time using AppMessageLayoutHelper
  )
}

internal fun ContourLayout.layoutTitle(title: BalancedLineTextView) {
  title.layoutBy(
    leftTo { parent.left() + 32.xdip }
      .rightTo { parent.right() - 32.xdip },
    emptyY() // Computed at model-setting time using AppMessageLayoutHelper
  )
}

internal fun ContourLayout.layoutSubtitle(subtitle: BalancedLineTextView) {
  subtitle.layoutBy(
    leftTo { parent.left() + 32.xdip }
      .rightTo { parent.right() - 32.xdip },
    emptyY() // Computed at model-setting time using AppMessageLayoutHelper
  )
}

internal fun ContourLayout.layoutActionsDivider(divider: View) {
  divider.layoutBy(
    leftTo { parent.left() }.rightTo { parent.right() },
    emptyY() // Computed at model-setting time using AppMessageLayoutHelper
  )
}

class AppMessageLayoutHelper(
  private val context: Context,
  private val topMargin: Int
) {
  fun imageTopMargin(image: AppMessageImage?): Int = when (image) {
    is Inset -> topMargin
    is Fill -> 0
    is Fixed -> topMargin
    null -> 0
  }

  fun titleTopMargin(
    image: AppMessageImage?,
    title: String?
  ): Int = if (title == null) 0 else when (image) {
    is Inset -> context.dip(24)
    is Fill -> context.dip(24)
    is Fixed -> context.dip(24)
    null -> context.dip(24)
  }

  fun subtitleTopMargin(
    image: AppMessageImage?,
    title: String?,
    subtitle: String?
  ): Int = when {
    subtitle == null -> 0
    title != null -> context.dip(4)
    else -> titleTopMargin(image = image, title = subtitle)
  }

  fun actionsTopMargin(
    messageHasText: Boolean,
    image: AppMessageImage?
  ): Int = if (messageHasText) {
    context.dip(28)
  } else when (image) {
    is Inset -> topMargin
    is Fill -> 0
    is Fixed -> topMargin
    null -> 0
  }
}
