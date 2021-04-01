// Copyright Square, Inc.
package app.cash.paparazzi.sample

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView.ScaleType.CENTER
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import app.cash.paparazzi.sample.Actions.One
import app.cash.paparazzi.sample.Actions.Two
import com.squareup.contour.ContourLayout

@SuppressLint("ViewConstructor")
class InlineAppMessageView constructor(
  context: Context,
  attrs: AttributeSet?,
  private val imageLoader: AppMessageImageLoader
) : ContourLayout(context, attrs) {

  private val image = AppCompatImageView(context).apply {
    scaleType = CENTER
  }
  private val messageTitle = BalancedLineTextView(context).styledAsTitle()
  private val messageSubtitle = BalancedLineTextView(context).styledAsSubtitle()
  private val actionsTopDivider = View(context).apply {
    setBackgroundColor(Color.WHITE)
  }
  private val actionsMiddleDivider = View(context).apply {
    setBackgroundColor(Color.WHITE)
  }
  private val leftActionButton = AppCompatTextView(context).styledAsActionButton()
  private val rightActionButton = AppCompatTextView(context).styledAsActionButton()

  private val layoutHelper = AppMessageLayoutHelper(
      topMargin = 24.dip,
      context = context
  )

  init {
    contourHeightOf {
      leftActionButton.bottom()
    }

    setBackgroundColor(Color.GRAY)

    layoutImage(image)
    layoutTitle(messageTitle)
    layoutSubtitle(messageSubtitle)
    layoutActionsDivider(actionsTopDivider)
    actionsMiddleDivider.layoutBy(
        x = leftTo { parent.centerX() }.widthOf { 1.xdip },
        y = topTo { actionsTopDivider.bottom() }.bottomTo { parent.bottom() }
    )
    rightActionButton.layoutBy(
        x = leftTo { actionsMiddleDivider.right() }.rightTo { parent.right() },
        y = topTo { actionsTopDivider.bottom() }
    )
    leftActionButton.layoutBy(
        x = emptyX(),
        y = emptyY()
    )
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()

    layoutTransition = LayoutTransition().apply {
      disableTransitionType(LayoutTransition.CHANGING)
    }
  }

  private fun InlineAppMessageViewModel.renderActions() = when (val actions = actions) {
    is One -> {
      actionsMiddleDivider.visibility = GONE
      rightActionButton.visibility = GONE

      // Lay out left button to occupy the full horizontal space
      leftActionButton.updateLayoutBy(
          x = leftTo { parent.left() }.rightTo { parent.right() },
          y = topTo { actionsTopDivider.bottom() }
      )

      actions.action.render(leftActionButton)
    }
    is Two -> {
      actionsMiddleDivider.visibility = VISIBLE
      rightActionButton.visibility = VISIBLE

      // Lay out the left button to occupy only half the horizontal space
      leftActionButton.updateLayoutBy(
          x = leftTo { parent.left() }.rightTo { actionsMiddleDivider.left() },
          y = topTo { actionsTopDivider.bottom() }
      )

      actions.primary.render(rightActionButton)
      actions.secondary.render(leftActionButton)
    }
  }

  fun setModel(model: InlineAppMessageViewModel) {
    messageTitle.text = model.title
    messageSubtitle.text = model.subtitle
    messageTitle.visibility = if (model.title != null) VISIBLE else GONE
    messageSubtitle.visibility = if (model.subtitle != null) VISIBLE else GONE

    model.renderActions()

    image.updateLayoutBy(
        y = topTo {
          parent.top() + layoutHelper.imageTopMargin(
              image = model.image
          )
        }
    )
    messageTitle.updateLayoutBy(
        y = topTo {
          image.bottom() + layoutHelper.titleTopMargin(
              image = model.image,
              title = model.title
          )
        }
    )
    messageSubtitle.updateLayoutBy(
        y = topTo {
          messageTitle.bottom() + layoutHelper.subtitleTopMargin(
              image = model.image,
              title = model.title,
              subtitle = model.subtitle
          )
        }
    )
    actionsTopDivider.updateLayoutBy(
        y = topTo {
          messageSubtitle.bottom() + layoutHelper.actionsTopMargin(
              messageHasText = model.title != null || model.subtitle != null,
              image = model.image
          )
        }.heightOf { 1.ydip }
    )

    messageSubtitle.apply {
      applyStyle(if (model.title == null) TextStyles.mainBody else TextStyles.smallBody)
      messageSubtitle.setTextColor(Color.BLACK)
    }
    imageLoader.load(
        image = model.image,
        imageView = image
    )
  }
}

private fun Action.render(button: AppCompatTextView) {
  button.text = text
  button.setTextColor(Color.BLACK)
}

