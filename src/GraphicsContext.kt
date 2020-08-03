
interface GraphicsContext {
    fun clear(colour: Colour)
    fun rectangle(rectangle: Rectangle, colour: Colour)
    fun line(start: Position, finish: Position, colour: Colour)
    fun write(text: String, rectangle: Rectangle, colour: Colour, alignment: Alignment = Alignment.Centre)
}
