// Copyright Square, Inc.
package app.cash.paparazzi.sample

sealed class Actions {
  data class One(val action: Action) : Actions()
  data class Two(val primary: Action, val secondary: Action) : Actions()
}

data class Action(
  val text: String,
  val color: Color?,
)
