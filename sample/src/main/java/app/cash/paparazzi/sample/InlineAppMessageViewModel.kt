// Copyright Square, Inc.
package app.cash.paparazzi.sample

data class InlineAppMessageViewModel(
  val messageToken: String,
  val image: AppMessageImage?,
  val title: String?,
  val subtitle: String?,
  val actions: Actions
)
