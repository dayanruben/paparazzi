package app.cash.paparazzi.sample

sealed class AppMessageImage {
  abstract val assetUrl: String

  data class Inset(
    override val assetUrl: String
  ) : AppMessageImage()

  data class Fill(
    override val assetUrl: String
  ) : AppMessageImage()

  data class Fixed(
    override val assetUrl: String,
    val width: Int,
    val height: Int
  ) : AppMessageImage()
}
