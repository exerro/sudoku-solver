package framework

import java.util.*
import kotlin.math.sqrt

/** RGBA colour object. */
@Suppress("UNUSED_PARAMETER")
class Colour private constructor(
        val red: Float,
        val green: Float,
        val blue: Float,
        val alpha: Float
) {
    fun withAlpha(alpha: Float) = Colour(red, green, blue, alpha)

    override fun toString() = "Colour($red, $green, $blue, $alpha)"
    override fun hashCode() = Objects.hash(red, green, blue, alpha)
    override fun equals(other: Any?): Boolean {
        val c = other as? Colour ?: return false
        return red == c.red && green == c.green && blue == c.blue && alpha == c.alpha
    }

    companion object {
        val red = rgb(0.8, 0.2, 0.2)
        val green = rgb(0.2, 0.8, 0.2)
        val blue = rgb(0.0, 0.4, 0.9)

        val yellow = rgb(0.9, 0.9, 0.3)
        val orange = rgb(0.9, 0.6, 0.3)
        val pink = rgb(0.9, 0.3, 0.9)
        val purple = rgb(0.45, 0.0, 0.7)
        val cyan = rgb(0.3, 0.6, 0.9)

        val white = greyscale(0.95)
        val ultraLightGrey = greyscale(0.85)
        val lightGrey = greyscale(0.75)
        val lighterGrey = rgb(0.59, 0.58, 0.6)
        val grey = rgb(0.33, 0.33, 0.35)
        val darkGrey = rgb(0.19, 0.19, 0.2)
        val charcoal = rgb(0.13, 0.13, 0.14)
        val black = greyscale(0.1)

        /** Create a colour from its red, green, blue and alpha components. */
        fun rgba(red: Float, green: Float, blue: Float, alpha: Float) = Colour(red, green, blue, alpha)
        /** Create a colour from its red, green, and blue components. */
        fun rgb(red: Float, green: Float, blue: Float, ignored: Float = 1f) = Colour(red, green, blue, 1f)
        /** Create a greyscale colour from its brightness and optional alpha component. */
        fun greyscale(value: Float, alpha: Float = 1f) = Colour(value, value, value, alpha)

        /** Create a colour from its red, green, blue and alpha components. */
        fun rgba(red: Double, green: Double, blue: Double, alpha: Double) = Colour(red.toFloat(), green.toFloat(), blue.toFloat(), alpha.toFloat())
        /** Create a colour from its red, green, and blue components. */
        fun rgb(red: Double, green: Double, blue: Double, ignored: Double = 1.0) = rgba(red, green, blue, 1.0)
        /** Create a greyscale colour from its brightness and optional alpha component. */
        fun greyscale(value: Double, alpha: Double = 1.0) = rgba(value, value, value, alpha)

        fun mix(ratio: Float, a: Colour, b: Colour): Colour {
            val red = sqrt(a.red * a.red * (1 - ratio) + b.red * b.red * ratio)
            val green = sqrt(a.green * a.green * (1 - ratio) + b.green * b.green * ratio)
            val blue = sqrt(a.blue * a.blue * (1 - ratio) + b.blue * b.blue * ratio)
            val alpha = a.alpha * (1 - ratio) + b.alpha * ratio
            return Colour(red, green, blue, alpha)
        }
    }
}

/** Collections of colours. */
object Colours {
    /** All the white, grey and black colours ordered lightest to darkest. */
    val greyscale = listOf(Colour.white, Colour.ultraLightGrey, Colour.lightGrey, Colour.lighterGrey, Colour.grey, Colour.darkGrey, Colour.charcoal, Colour.black)
    /** All the pre-defined colours. */
    val all = greyscale + listOf(Colour.red, Colour.green, Colour.blue, Colour.yellow, Colour.pink, Colour.purple, Colour.cyan)
}
