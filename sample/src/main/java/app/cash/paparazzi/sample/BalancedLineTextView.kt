// Copyright Square, Inc.
package app.cash.paparazzi.sample

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.roundToInt

/**
 * A TextView that balances the number of characters on each line when it has multiple lines.
 * This isn't a fully robust implementation, but works for the basic cases. If the TextView
 * changes sizes, it won't be rebalanced (though this functionality could be added).
 * The balancing doesn't take into account measured widths of characters and uses character
 * count as a good enough approximation. BalancedLineTextView doesn't support text views
 * with spannables as it overwrites the text (which drops the spannable).
 */
class BalancedLineTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
  private var compatLineSpacingExtra = 0f
  private var compatLineSpacingMultiplier = 0f
  private var formatting = false
  private var layoutListener: OnGlobalLayoutListener? = null

  /** The latest text value without any balancing applied. */
  private var unbalancedText: CharSequence? = null

  /** Will add line breaks until there are at least this many lines */
  var preferredLineCount = 1
    set(value) {
      field = value
      updateBreakStrategy()
    }

  init {
    compatLineSpacingMultiplier = lineSpacingMultiplier
    compatLineSpacingExtra = lineSpacingExtra

    updateBreakStrategy()
  }

  fun shouldBalanceManually(): Boolean {
    return Build.VERSION.SDK_INT < 23 || preferredLineCount > 1
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    addGlobalLayoutListener()
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (layoutListener != null && viewTreeObserver.isAlive) {
      viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
    }
  }

  /**
   * Only sets the text if it is different than the current value (pre-balancing).
   */
  fun setTextIfChanged(text: CharSequence?) {
    if (text != unbalancedText) {
      setText(text)
    }
  }

  override fun setLineSpacing(
    add: Float,
    mult: Float
  ) {
    super.setLineSpacing(add, mult)
    if (!formatting) {
      compatLineSpacingExtra = add
      compatLineSpacingMultiplier = mult
    }
  }

  @SuppressLint("WrongConstant") // LineBreaker requires API 29.
  private fun updateBreakStrategy() {
    if (shouldBalanceManually()) {
      breakStrategy = Layout.BREAK_STRATEGY_HIGH_QUALITY
      hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NORMAL
    } else {
      breakStrategy = Layout.BREAK_STRATEGY_BALANCED
      hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
    }
  }

  private fun addGlobalLayoutListener() {
    if (viewTreeObserver.isAlive) {
      if (layoutListener == null) {
        layoutListener = object : OnGlobalLayoutListener {
          override fun onGlobalLayout() {
            if (!shouldBalanceManually() || lineCount < 2) {
              return
            }

            // Only run this code once per setText.
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            balanceText()
          }
        }
      }

      viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }
  }

  private fun balanceText() {
    formatting = true

    try {
      // Don't balance if we don't have a width set yet.
      if (width == 0) {
        return
      }

      val currentLineCount = lineCount
      val desiredLineCount = currentLineCount.coerceAtLeast(preferredLineCount)

      if (desiredLineCount < 2) {
        setLineSpacing(0f, 1f)
        return
      }

      setLineSpacing(compatLineSpacingExtra, compatLineSpacingMultiplier)

      val text = text.toString()
      if (text.contains("\n")) {
        // If the text has already been formatted, or the caller has manually formatted
        // the text, don't change it.
        return
      }
      val originalText = text.replace("\n", " ")
      val formattedText = getFormattedText(originalText, desiredLineCount)
      setText(formattedText)
    } finally {
      formatting = false
    }
  }

  // TODO(apaulin): Add unit tests for this method.
  private fun getFormattedText(
    originalText: String,
    lineCount: Int
  ): String {
    var formattedText = originalText
    var index = originalText.length
    val averageLineLength = index / lineCount
    var i = lineCount - 1
    while (i > 0) {
      index = findNearestSpaceIndex(originalText, i, i + 1, index)
      if (index < averageLineLength * (i - 1)) {
        // If there is a really long word that takes up multiple lines,
        // skip adding a line feed.
        i--
      }
      if (index != -1) {
        formattedText =
          formattedText.substring(0, index) + "\n" + formattedText.substring(index + 1)
      }
      i--
    }
    return formattedText
  }

  /**
   * Finds the index of the nearest space character to the pivot index. The pivot index is
   * calculated by taking the fraction of remaining characters in front of the last index.
   */
  private fun findNearestSpaceIndex(
    originalText: String,
    numerator: Int,
    denominator: Int,
    lastIndex: Int
  ): Int {
    val pivotIndex = ((lastIndex * numerator) / denominator.toFloat()).roundToInt()
    val candidateIndex1 = originalText.lastIndexOf(' ', pivotIndex)
    val candidateIndex2 = originalText.indexOf(' ', pivotIndex)
    val delta1 = if (candidateIndex1 != -1) pivotIndex - candidateIndex1 else Int.MAX_VALUE
    val delta2 = if (candidateIndex2 != -1) candidateIndex2 - pivotIndex else Int.MAX_VALUE
    return if (delta1 < delta2) candidateIndex1 else candidateIndex2
  }

  override fun onTextChanged(
    text: CharSequence?,
    start: Int,
    lengthBefore: Int,
    lengthAfter: Int
  ) {
    super.onTextChanged(text, start, lengthBefore, lengthAfter)
    if (!formatting) {
      unbalancedText = text

      if (shouldBalanceManually()) {
        // getLineCount() returns 0 now, so post the work to the next frame when we have a line count.
        post(this::balanceText)
      }
    }
  }
}
