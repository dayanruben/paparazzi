package app.cash.paparazzi.plugin.test

import android.content.Context
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.accessibility.AccessibilityRenderExtension
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import org.junit.Rule
import org.junit.Test

class ComposeA11yTest {
  @get:Rule
  val paparazzi = Paparazzi(
    theme = "Theme.AppCompat.Light.NoActionBar",
    deviceConfig = DeviceConfig.PIXEL,
    renderExtensions = setOf(AccessibilityRenderExtension())
  )

  @Test
  fun `mixed compose usage`() {
    val mixedView = MixedView(paparazzi.context)
    paparazzi.snapshot(mixedView)
  }

  @Test
  fun dropDownMaterial3() {
    paparazzi.snapshot {
      Box(Modifier.fillMaxSize()) {
        DropdownMenu(
          expanded = true,
          onDismissRequest = { }
        ) {
          DropdownMenuItem(
            text = {
              Text(
                text = "Label 1"
              )
            },
            onClick = {}
          )
          DropdownMenuItem(
            text = {
              Text(
                text = "Label 2"
              )
            },
            onClick = {}
          )
          DropdownMenuItem(
            text = {
              Text(
                text = "Label 3"
              )
            },
            onClick = {}
          )
        }
      }
    }
  }

  @Test
  @OptIn(ExperimentalMaterial3Api::class)
  fun modalBottomSheetMaterial3() {
    paparazzi.snapshot {
      ModalBottomSheet(
        onDismissRequest = {},
        sheetState = SheetState(
          skipPartiallyExpanded = true,
          density = LocalDensity.current,
          initialValue = SheetValue.Expanded
        )
      ) {
        Text(text = "Text 2")
      }
      Text(modifier = Modifier.wrapContentSize(), text = "Text 1")
    }
  }

  @Test
  fun `verify changing view hierarchy order doesn't change accessibility colors`() {
    val mixedView = MixedView(paparazzi.context).apply {
      addView(
        ComposeView(context).apply {
          id = 10
          setContent {
            Box(modifier = Modifier.size(50.dp)) {}
          }
        },
        0,
        LinearLayout.LayoutParams(0, 0)
      )
    }
    paparazzi.snapshot(mixedView)
  }

  @Test
  fun `verify clear and set semantics`() {
    paparazzi.snapshot {
      Box(modifier = Modifier.clickable {}) {
        Column(
          modifier = Modifier.clearAndSetSemantics {
            contentDescription = "OVERRIDDEN CONTENT DESCRIPTION"
          }
        ) {
          Text(text = "Text")
        }
      }
    }
  }

  @Test
  fun `verify hidden ComposeView content is not in legend`() {
    val view = ComposeView(paparazzi.context).apply {
      visibility = GONE
      setContent {
        Column {
          Text(text = "Text 1")
          Text(text = "Text 2")
        }
      }
    }

    paparazzi.snapshot(view)
  }

  @Test
  fun legendDoesNotScale() {
    paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL.copy(fontScale = 2.0f))
    paparazzi.snapshot {
      Column(Modifier.background(Color.LightGray)) {
        androidx.compose.material.Text("Some text that will appear scaled in the UI, but not scaled in the legend")
      }
    }
  }

  @Test(expected = IllegalStateException::class)
  fun renderingModeSHRINKThrowsException() {
    paparazzi.unsafeUpdateConfig(renderingMode = RenderingMode.SHRINK)
    paparazzi.snapshot {
      Column(Modifier.background(Color.LightGray)) {
        Text("SHRINK and AccessibilityRenderExtension are not supported together")
      }
    }
  }

  @Test
  fun `verify progress semantic information represented`() {
    val view = ComposeView(paparazzi.context).apply {
      setContent {
        Column {
          Slider(
            value = 0.5f,
            onValueChange = { _ -> },
            valueRange = 0f..1f
          )
          CircularProgressIndicator(
            modifier = Modifier.progressSemantics()
          )
          Slider(
            modifier = Modifier.semantics {
              setProgress("Adjust volume") { _ -> true }
            },
            value = 0.26f,
            onValueChange = { _ -> },
            valueRange = 0f..1f
          )
        }
      }
    }

    paparazzi.snapshot(view)
  }

  @Test
  fun `verify link annotation and custom actions`() {
    val view = ComposeView(paparazzi.context).apply {
      setContent {
        Column {
          // Test link annotation
          val annotatedString = buildAnnotatedString {
            append("Visit ")
            pushLink(LinkAnnotation.Url("https://www.example.com"))
            append("Url ")
            pop()
            pushLink(LinkAnnotation.Clickable(tag = "CLICK") {})
            append("Clickable")
            pop()
          }

          Text(text = annotatedString)

          // Test custom actions
          Box(
            modifier = Modifier
              .size(100.dp)
              .background(Color.LightGray)
              .semantics(mergeDescendants = true) {
                customActions = listOf(
                  CustomAccessibilityAction("Action 1") { true },
                  CustomAccessibilityAction("Action 2") { true }
                )
              }
          ) {
            Text("Box with custom actions")
          }
        }
      }
    }

    paparazzi.snapshot(view)
  }

  @Test
  fun `verify view custom actions`() {
    val view = buildViewWithCustomActions(paparazzi.context)
    paparazzi.snapshot(view, name = "custom-actions")
  }

  private fun buildViewWithCustomActions(context: Context) =
    LinearLayout(context).apply {
      addView(
        Button(context).apply {
          text = "Actions Button"
          ViewCompat.setAccessibilityDelegate(
            this,
            object : AccessibilityDelegateCompat() {
              override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.addAction(
                  AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                    AccessibilityNodeInfoCompat.ACTION_CLICK,
                    "Custom Click Action"
                  )
                )
                info.addAction(
                  AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                    AccessibilityNodeInfoCompat.ACTION_LONG_CLICK,
                    "Custom Long Press Action"
                  )
                )
              }
            }
          )
        }
      )
    }
}
