package app.cash.paparazzi.sample

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat

fun View.dip(value: Int): Int =
  TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value.toFloat(),
      context.resources.displayMetrics
  ).toInt()

fun Context.dip(value: Int): Int =
  TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value.toFloat(),
      resources.displayMetrics
  ).toInt()


fun Context.dip(value: Float): Float =
  TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value,
      resources.displayMetrics
  )

fun Context.sp(value: Float): Float =
  TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      value,
      resources.displayMetrics
  )

fun TextView.applyStyle(themeInfo: TextThemeInfo) = apply {
  themeInfo.textColor?.let {
    setTextColor(it)
    setLinkTextColor(it)
  }
  textSizeInPx = themeInfo.textSize.toPx(context)
  letterSpacing = themeInfo.letterSpacing

  val lineHeightPx = themeInfo.lineHeight.toPx(context).toInt()
  if (this is LineHeightReceiver) {
    lineHeight = lineHeightPx
  } else {
    TextViewCompat.setLineHeight(this, lineHeightPx)
  }
}

data class TextThemeInfo(
  @Deprecated("Colors vary by theme, while metrics and fonts do not. Please use ColorPalette.")
  @ColorInt val textColor: Int? = null,
  val textSize: Dimen,
  val lineHeight: Dimen,
  val letterSpacing: Float
)

sealed class Dimen {
  abstract fun toPx(context: Context): Float

  class Dp(val value: Int) : Dimen() {
    override fun toPx(context: Context) = context.dip(value.toFloat())
  }

  class Sp(val value: Int) : Dimen() {
    override fun toPx(context: Context) = context.sp(value.toFloat())
  }
}

var TextView.textSizeInPx: Float
  get() = textSize
  set(value) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
  }

interface LineHeightReceiver {
  var lineHeight: Int?
}
