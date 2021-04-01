// Copyright Square, Inc.
package app.cash.paparazzi.sample

import android.graphics.Color
import android.view.Gravity
import android.view.View.TEXT_ALIGNMENT_CENTER
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updatePadding

internal fun BalancedLineTextView.styledAsTitle(): BalancedLineTextView {
  gravity = Gravity.CENTER_HORIZONTAL
  applyStyle(TextStyles.mainTitle)
  setTextColor(Color.BLACK)
  updatePadding(
    left = dip(16),
    right = dip(16)
  )

  return this
}

internal fun BalancedLineTextView.styledAsSubtitle(): BalancedLineTextView {
  gravity = Gravity.CENTER_HORIZONTAL
  updatePadding(
    left = dip(16),
    right = dip(16)
  )

  return this
}

internal fun AppCompatTextView.styledAsActionButton(): AppCompatTextView {
  gravity = Gravity.CENTER
  textAlignment = TEXT_ALIGNMENT_CENTER
  isClickable = true

  applyStyle(TextStyles.mainTitle)

  updatePadding(
    top = dip(16),
    bottom = dip(16),
    left = dip(16),
    right = dip(16)
  )

  return this
}