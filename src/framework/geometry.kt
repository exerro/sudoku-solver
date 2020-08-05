package framework

import kotlin.math.min
import kotlin.math.roundToInt

/** 2D position relative to the origin. */
data class Position(val x: Float, val y: Float = x) {
    companion object {
        val origin = Position(0f, 0f)
    }
}

/** 2D offset from a [Position]. */
data class Size(val width: Float, val height: Float = width) {
    companion object {
        val zero = Size(0f, 0f)
        val one = Size(1f, 1f)
        val horizontal = Size(1f, 0f)
        val vertical = Size(0f, 1f)
    }
}

/** Rectangle bounded by a position and a size. */
data class Rectangle(val position: Position, val size: Size) {
    constructor(position: Position, width: Float, height: Float = width): this(position, Size(width, height))
    constructor(x: Float, y: Float, width: Float, height: Float = width): this(Position(x, y), width, height)
    constructor(topLeft: Position, bottomRight: Position): this(topLeft, bottomRight - topLeft)
}

////////////////////////////////////////////////////////////////////////////////

operator fun Position.plus(size: Size) = Position(x + size.width, y + size.height)
operator fun Position.minus(size: Size) = Position(x - size.width, y - size.height)
operator fun Position.minus(position: Position) = Size(x - position.x, y - position.y)
fun Position.rounded() = Position(x.roundToInt().toFloat(), y.roundToInt().toFloat())

////////////////////////////////////////////////////////////////////////////////

operator fun Size.plus(size: Size) = Size(width + size.width, height + size.height)
operator fun Size.minus(size: Size) = Size(width - size.width, height - size.height)
operator fun Size.times(size: Size) = Size(width * size.width, height * size.height)
operator fun Size.times(scale: Float) = Size(width * scale, height * scale)
operator fun Size.div(size: Size) = Size(width / size.width, height / size.height)
operator fun Size.div(scale: Float) = Size(width / scale, height / scale)
val Size.horizontal get() = Size(width, 0f)
val Size.vertical get() = Size(0f, height)

////////////////////////////////////////////////////////////////////////////////

/** Left edge's X value. */
val Rectangle.left get() = position.x
/** Right edge's X value. */
val Rectangle.right get() = position.x + size.width
/** Top edge's Y value. */
val Rectangle.top get() = position.y
/** Bottom edge's Y value. */
val Rectangle.bottom get() = position.y + size.height
/** Top left corner. */
val Rectangle.topLeft get() = position
/** Bottom right corner. */
val Rectangle.bottomRight get() = position + size

//////////////////////////////////////////////////////////////////////////////////////////

/** Return a square that is sized equal to the minimum of the width and height. */
fun Rectangle.minSquare(alignX: Float = 0.5f, alignY: Float = 0.5f): Rectangle {
    val newSize = Size(min(size.width, size.height))
    return Rectangle(position + (size - newSize) * Size(alignX, alignY), newSize)
}

////////////////////////////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////////////////////

fun Rectangle.translate(dx: Float, dy: Float) = Rectangle(position + Size(dx, dy), size)
fun Rectangle.translateHorizontal(dx: Float) = translate(dx, 0f)
fun Rectangle.translateVertical(dy: Float) = translate(0f, dy)
fun Rectangle.translateBy(sx: Float, sy: Float) = Rectangle(position + size * Size(sx, sy), size)
fun Rectangle.translateHorizontalBy(sx: Float) = translateBy(sx, 0f)
fun Rectangle.translateVerticalBy(sy: Float) = translateBy(0f, sy)

////////////////////////////////////////////////////////////////////////////////

/** Split a rectangle vertically into two using [ratio]. */
fun Rectangle.splitVertical(ratio: Float = 0.5f): Pair<Rectangle, Rectangle> =
        Rectangle(position, Size(size.width, size.height * ratio)) to
        Rectangle(position + size.vertical * ratio, Size(size.width, size.height * (1 - ratio)))

/** Split a rectangle vertically into [divisions] equally sized rectangles. */
fun Rectangle.splitVertical(divisions: Int): List<Rectangle> {
    val delta = size.vertical / divisions.toFloat()
    val size = Size(size.width, size.height / divisions)
    return (0 until divisions).map { Rectangle(position + delta * it.toFloat(), size) }
}

/** Split a rectangle horizontally into two using [ratio]. */
fun Rectangle.splitHorizontal(ratio: Float = 0.5f): Pair<Rectangle, Rectangle> =
        Rectangle(position, Size(size.width * ratio, size.height)) to
        Rectangle(position + size.horizontal * ratio, Size(size.width * (1 - ratio), size.height))

/** Split a rectangle horizontally into [divisions] equally sized rectangles. */
fun Rectangle.splitHorizontal(divisions: Int): List<Rectangle> {
    val delta = size.horizontal / divisions.toFloat()
    val size = Size(size.width / divisions, size.height)
    return (0 until divisions).map { Rectangle(position + delta * it.toFloat(), size) }
}
