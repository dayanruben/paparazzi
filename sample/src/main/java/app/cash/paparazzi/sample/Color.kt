package app.cash.paparazzi.sample

class Color(
  @JvmField val light: ModeVariant? = null,
  @JvmField val dark: ModeVariant? = null
) {
  class ModeVariant(@JvmField val srgb: String? = null)
}