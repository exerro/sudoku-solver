package framework
data class Colour(
        val red: Float,
        val green: Float,
        val blue: Float,
        val alpha: Float = 1f
) {
    constructor(greyscale: Float, alpha: Float = 1f):
            this(greyscale, greyscale, greyscale, alpha)

    constructor(r: Double, g: Double, b: Double):
            this(r.toFloat(), g.toFloat(), b.toFloat())

    companion object {
        val red = Colour(0.8, 0.2, 0.2)
        val green = Colour(0.2, 0.8, 0.2)
        val blue = Colour(0.0, 0.4, 0.9)

        val yellow = Colour(0.9, 0.9, 0.3)
        val pink = Colour(0.9, 0.3, 0.9)
        val purple = Colour(0.45, 0.0, 0.7)
        val cyan = Colour(0.3, 0.6, 0.9)

        val white = Colour(0.95f)
        val ultraLightGrey = Colour(0.85f)
        val lightGrey = Colour(0.75f)
        val lighterGrey = Colour(0.59f, 0.58f, 0.6f)
        val grey = Colour(0.33f, 0.33f, 0.35f)
        val darkGrey = Colour(0.19f, 0.19f, 0.2f)
        val charcoal = Colour(0.13f, 0.13f, 0.14f)
        val black = Colour(0.1f)
    }
}
