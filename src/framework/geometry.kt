package framework
data class Position(val x: Float, val y: Float = x)
data class Size(val width: Float, val height: Float = width)
data class Rectangle(val position: Position, val size: Size) {
    constructor(x: Float, y: Float, width: Float, height: Float): this(Position(x, y), Size(width, height))
    constructor(position: Float, size: Float): this(Position(position), Size(size))
}

operator fun Position.plus(size: Size) = Position(x + size.width, y + size.height)
operator fun Position.minus(size: Size) = Position(x - size.width, y - size.height)
operator fun Position.minus(position: Position) = Size(x - position.x, y - position.y)

operator fun Size.plus(size: Size) = Size(width + size.width, height + size.height)
operator fun Size.minus(size: Size) = Size(width - size.width, height - size.height)
operator fun Size.times(size: Size) = Size(width * size.width, height * size.height)
operator fun Size.times(scale: Float) = Size(width * scale, height * scale)
operator fun Size.div(size: Size) = Size(width / size.width, height / size.height)
operator fun Size.div(scale: Float) = Size(width / scale, height / scale)

infix fun Position.rectTo(position: Position) = Rectangle(this, position - this)
val Rectangle.left get() = position.x
val Rectangle.right get() = position.x + size.width
val Rectangle.top get() = position.y
val Rectangle.bottom get() = position.y + size.height
val Rectangle.topLeft get() = position
val Rectangle.bottomRight get() = position + size
