
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
        val lightGrey = Colour(0.8f)
        val grey = Colour(0.35f)
        val darkGrey = Colour(0.15f)
        val charcoal = Colour(0.1f)
        val black = Colour(0.03f)
    }
}
