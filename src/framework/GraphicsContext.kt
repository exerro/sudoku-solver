package framework

import framework.*

interface GraphicsContext {
    fun begin()
    fun clear(colour: Colour)
    fun rectangle(rectangle: Rectangle, colour: Colour)
    fun line(start: Position, finish: Position, colour: Colour, thickness: Float = 1f)
    fun write(text: String, rectangle: Rectangle, colour: Colour, alignment: Alignment = Alignment.Centre)
    fun finish()

    fun rectangleOutline(rectangle: Rectangle, colour: Colour, thickness: Float = 1f) {
        val a = rectangle.position
        val b = a + Size(0f, rectangle.size.height)
        val c = a + rectangle.size
        val d = a + Size(rectangle.size.width, 0f)

        line(a, b, colour, thickness)
        line(b, c, colour, thickness)
        line(c, d, colour, thickness)
        line(d, a, colour, thickness)
    }
}
