package framework

import com.exerro.glfw.data.WindowPosition
import com.exerro.glfw.data.WindowSize
import kotlin.math.min

data class Position(val x: Float, val y: Float = x) {
    companion object {
        val origin = Position(0f, 0f)
    }
}
data class Size(val width: Float, val height: Float = width) {
    companion object {
        val zero = Size(0f, 0f)
        val one = Size(1f, 1f)
        val horizontal = Size(1f, 0f)
        val vertical = Size(0f, 1f)
    }
}
data class Rectangle(val position: Position, val size: Size) {
    constructor(x: Float, y: Float, width: Float, height: Float): this(Position(x, y), Size(width, height))
    constructor(position: Float, size: Float): this(Position(position), Size(size))
}

operator fun Position.plus(size: Size) = Position(x + size.width, y + size.height)
operator fun Position.minus(size: Size) = Position(x - size.width, y - size.height)
operator fun Position.minus(position: Position) = Size(x - position.x, y - position.y)
fun Position.rounded() = Position(x.toInt().toFloat(), y.toInt().toFloat())

operator fun Size.plus(size: Size) = Size(width + size.width, height + size.height)
operator fun Size.minus(size: Size) = Size(width - size.width, height - size.height)
operator fun Size.times(size: Size) = Size(width * size.width, height * size.height)
operator fun Size.times(scale: Float) = Size(width * scale, height * scale)
operator fun Size.div(size: Size) = Size(width / size.width, height / size.height)
operator fun Size.div(scale: Float) = Size(width / scale, height / scale)
val Size.horizontal get() = Size(width, 0f)
val Size.vertical get() = Size(0f, height)

infix fun Position.rectTo(position: Position) = Rectangle(this, position - this)
fun Rectangle.minSquare(alignX: Float = 0.5f, alignY: Float = 0.5f): Rectangle {
    val newSize = Size(min(size.width, size.height))
    return Rectangle(position + (size - newSize) * Size(alignX, alignY), newSize)
}
val Rectangle.left get() = position.x
val Rectangle.right get() = position.x + size.width
val Rectangle.top get() = position.y
val Rectangle.bottom get() = position.y + size.height
val Rectangle.topLeft get() = position
val Rectangle.bottomRight get() = position + size

fun Rectangle.resize(size: Size, alignX: Float = 0.5f, alignY: Float = 0.5f) =
        Rectangle(position + (this.size - size) * Size(alignX, alignY), size)
fun Rectangle.resizeHorizontal(width: Float, alignment: Float = 0.5f) =
        resize(Size(width, size.height), alignment, 0f)
fun Rectangle.resizeVertical(height: Float, alignment: Float = 0.5f) =
        resize(Size(size.width, height), 0f, alignment)
fun Rectangle.resizeBy(scale: Float, alignX: Float = 0.5f, alignY: Float = 0.5f) =
        resize(size * scale, alignX, alignY)
fun Rectangle.resizeHorizontalBy(scale: Float, alignment: Float = 0.5f) =
        resizeHorizontal(size.width * scale, alignment)
fun Rectangle.resizeVerticalBy(scale: Float, alignment: Float = 0.5f) =
        resizeVertical(size.height * scale, alignment)

fun Rectangle.translate(dx: Float, dy: Float) = Rectangle(position + Size(dx, dy), size)
fun Rectangle.translateHorizontal(dx: Float) = translate(dx, 0f)
fun Rectangle.translateVertical(dy: Float) = translate(0f, dy)
fun Rectangle.translateBy(sx: Float, sy: Float) = Rectangle(position + size * Size(sx, sy), size)
fun Rectangle.translateHorizontalBy(sx: Float) = translateBy(sx, 0f)
fun Rectangle.translateVerticalBy(sy: Float) = translateBy(0f, sy)

fun Rectangle.splitVertical(ratio: Float = 0.5f): Pair<Rectangle, Rectangle> =
        Rectangle(position, Size(size.width, size.height * ratio)) to
        Rectangle(position + size.vertical * ratio, Size(size.width, size.height * (1 - ratio)))
fun Rectangle.splitVertical(divisions: Int): List<Rectangle> {
    val delta = size.vertical / divisions.toFloat()
    val size = Size(size.width, size.height / divisions)
    return (0 until divisions).map { Rectangle(position + delta * it.toFloat(), size) }
}
fun Rectangle.splitHorizontal(ratio: Float = 0.5f): Pair<Rectangle, Rectangle> =
        Rectangle(position, Size(size.width * ratio, size.height)) to
                Rectangle(position + size.horizontal * ratio, Size(size.width * (1 - ratio), size.height))
fun Rectangle.splitHorizontal(divisions: Int): List<Rectangle> {
    val delta = size.horizontal / divisions.toFloat()
    val size = Size(size.width / divisions, size.height)
    return (0 until divisions).map { Rectangle(position + delta * it.toFloat(), size) }
}

//////////////////////////////////////////////////////////////////////////////////////////

val WindowPosition.pos get() = Position(x.toFloat(), y.toFloat())
