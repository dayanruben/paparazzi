/*
 * Copyright (C) 2019 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.paparazzi.sample

import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_2
import app.cash.paparazzi.sample.Actions.Two
import app.cash.paparazzi.sample.AppMessageImage.Fill
import app.cash.paparazzi.sample.AppMessageImage.Fixed
import app.cash.paparazzi.sample.AppMessageImage.Inset
import org.junit.Rule
import org.junit.Test

class AppMessageViewTest {
  @get:Rule
  val paparazzi = Paparazzi(deviceConfig = PIXEL_2)

  @Test
  fun test() {
    val view = InlineAppMessageView(
        context = paparazzi.context,
        attrs = null,
        imageLoader = FakeImageLoader()
    )

    val viewModel = DEFAULT.copy(
        image = INSET_IMAGE,
        title = null,
        subtitle = "This is a longer description of the message we are displaying"
    )

    view.setModel(viewModel)
    paparazzi.snapshot(view)
  }

  companion object {
    private val DEFAULT = InlineAppMessageViewModel(
        messageToken = "myMessageToken",
        image = null,
        title = null,
        subtitle = null,
        actions = Two(
            primary = Action(
                text = "Action 1",
                color = null,
            ),
            secondary = Action(
                text = "Action 2",
                color = null,
            )
        )
    )
    private val FIXED_IMAGE = Fixed(assetUrl = "fixed_image", width = 150, height = 150)
    private val INSET_IMAGE = Inset(assetUrl = "inset_image")
    private val FILL_IMAGE = Fill(assetUrl = "fill_image")
  }
}
