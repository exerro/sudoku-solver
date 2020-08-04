package framework

/** Context object allowing graphical operations. */
interface GraphicsContext {
    /** Begin a batch of drawing operations. This will likely be called
     *  externally. There is no constraint on the number of times this is
     *  called before operations are presented to the screen, but each [begin()]
     *  must be matched by a [finish()]. */
    fun begin()

    /** Clear the screen, filling it with [colour]. */
    fun clear(colour: Colour)

    /** Fill a [rectangle] with the [colour] given. */
    fun rectangle(rectangle: Rectangle, colour: Colour)

    /** Draw a line between [start] and [finish] points. The colour of the first
     *  point will be [colour], and this will fade linearly to [colour2] at the
     *  second point. If no [colour2] is given, the line will be uniformly
     *  coloured. The line will be [thickness] pixels in cross-section
     *  (perpendicular to the line's direction). */
    fun line(start: Position, finish: Position, colour: Colour, thickness: Float = 1f, colour2: Colour = colour)

    /** Write [text] on the screen in the given [colour]. Text will be
     *  vertically centred within the [rectangle] and aligned horizontally
     *  within the rectangle according to [alignment]. */
    fun write(text: String, rectangle: Rectangle, colour: Colour, alignment: Alignment = Alignment.Centre)

    /** Finish drawing. This will likely be called externally. This must be
     *  called after a previous call to [begin()]] */
    fun finish()

    /** Draw the outline of a rectangle with the [colour] given. The [thickness]
     *  controls the lines of this outline as with [line()]. */
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
