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
operator fun Size.unaryMinus() = Size(-width, -height)
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
/** Centre of the rectangle. */
val Rectangle.centre get() = position + size / 2f

/** Top left corner. */
val Rectangle.topLeft get() = position
/** Top left corner. */
val Rectangle.topRight get() = position + size.horizontal
/** Bottom right corner. */
val Rectangle.bottomLeft get() = position + size.vertical
/** Bottom right corner. */
val Rectangle.bottomRight get() = position + size

//////////////////////////////////////////////////////////////////////////////////////////

/** Return the largest square that exists within this rectangle, aligning it
 *  vertically or horizontally depending on the dimensions of this rectangle. */
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

fun Rectangle.translateBy(dx: Float, dy: Float) = Rectangle(position + Size(dx, dy), size)
fun Rectangle.translateHorizontalBy(dx: Float) = translateBy(dx, 0f)
fun Rectangle.translateVerticalBy(dy: Float) = translateBy(0f, dy)
fun Rectangle.translateRelative(sx: Float, sy: Float) = Rectangle(position + size * Size(sx, sy), size)
fun Rectangle.translateHorizontalRelative(sx: Float) = translateRelative(sx, 0f)
fun Rectangle.translateVerticalRelative(sy: Float) = translateRelative(0f, sy)

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

////////////////////////////////////////////////////////////////////////////////

/** Return true if [position] lies within the rectangle, or false otherwise. */
fun Rectangle.contains(position: Position) = when {
    position.x < this.position.x -> false
    position.y < this.position.y -> false
    position.x > this.position.x + size.width -> false
    position.y > this.position.y + size.height -> false
    else -> true
}
