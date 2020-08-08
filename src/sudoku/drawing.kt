package sudoku

import framework.*

/** Draw the major and minor lines of a sudoku grid [settings] controls the
 *  colour and thickness of the major and minor lines but defaults to using 2px
 *  grey and 1px lighterGrey respectively. */
fun GraphicsContext.drawGridlines(
        rectangle: Rectangle,
        settings: DrawGridSettings = DrawGridSettings.DEFAULT
) {
    val graphics = this

    for (i in 0 .. 9) {
        val hp1 = rectangle.position + rectangle.size.horizontal * (i / 9f)
        val hp2 = rectangle.position + rectangle.size.vertical + rectangle.size.horizontal * (i / 9f)
        val vp1 = rectangle.position + rectangle.size.vertical * (i / 9f)
        val vp2 = rectangle.position + rectangle.size.horizontal + rectangle.size.vertical * (i / 9f)

        graphics.line(hp1.rounded(), hp2.rounded(), settings.minorLineColour, settings.minorLineThickness)
        graphics.line(vp1.rounded(), vp2.rounded(), settings.minorLineColour, settings.minorLineThickness)
    }

    for (i in 0 .. 3) {
        val hp1 = rectangle.position + rectangle.size.horizontal * (i / 3f)
        val hp2 = rectangle.position + rectangle.size.vertical + rectangle.size.horizontal * (i / 3f)
        val vp1 = rectangle.position + rectangle.size.vertical * (i / 3f)
        val vp2 = rectangle.position + rectangle.size.horizontal + rectangle.size.vertical * (i / 3f)

        graphics.line(hp1.rounded(), hp2.rounded(), settings.majorLineColour, settings.majorLineThickness)
        graphics.line(vp1.rounded(), vp2.rounded(), settings.majorLineColour, settings.majorLineThickness)
    }
}

/** Draw a sudoku grid with no grid lines within [rectangle] by drawing the
 *  items within the grid. [drawItem] accepts the rectangle to draw within, and
 *  the location of the item within the grid. */
fun GraphicsContext.drawGrid(
        rectangle: Rectangle,
        drawItem: (area: Rectangle, location: GridLocation) -> Unit
) {
    for (row in 0 .. 8) {
        for (col in 0 .. 8) {
            val size = rectangle.size / 9f
            val offset = rectangle.position + size * Size(col.toFloat(), row.toFloat())
            val location = GridLocation.fromRowAndColumn(row, col)
            drawItem(Rectangle(offset, size), location)
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

fun GraphicsContext.drawGridOffsetLines(
        rectangle: Rectangle,
        settings: DrawGridOffsetSettings = DrawGridOffsetSettings.DEFAULT
) {
    val graphics = this
    val maxHDistance = Grid.COLUMNS - 1f
    val maxVDistance = Grid.ROWS - 1f
    val maxDistance = Grid.COLUMNS + Grid.ROWS - 2f
    val unitSize = rectangle.size / Size(maxHDistance * 2 + 1, maxVDistance * 2 + 1)
    val centreRect = rectangle.resize(unitSize)
    val centre = rectangle.centre

    // assumes columns = rows
    for (i in 0 until Grid.COLUMNS) {
        val closeColour = Colour.mix(i / maxDistance, settings.primaryColour, settings.fadeColour)
        val farColour = Colour.mix((i + maxVDistance) / maxDistance, settings.primaryColour, settings.fadeColour)

        for ((origin, dp, ds) in listOf(
                centreRect.topLeft to -unitSize.horizontal to unitSize.vertical * -maxVDistance,
                centreRect.topRight to unitSize.horizontal to unitSize.vertical * -maxVDistance,
                centreRect.bottomLeft to -unitSize.horizontal to unitSize.vertical * maxVDistance,
                centreRect.bottomRight to unitSize.horizontal to unitSize.vertical * maxVDistance,
                centreRect.topLeft to -unitSize.vertical to unitSize.horizontal * -maxVDistance,
                centreRect.topRight to -unitSize.vertical to unitSize.horizontal * maxVDistance,
                centreRect.bottomLeft to unitSize.vertical to unitSize.horizontal * -maxVDistance,
                centreRect.bottomRight to unitSize.vertical to unitSize.horizontal * maxVDistance
        )) {
            val p0 = origin + dp * i.toFloat()
            graphics.line(p0, p0 + ds, closeColour, settings.thickness, farColour)
        }

        for ((dp, ds) in listOf(
                -unitSize.vertical to unitSize.horizontal / 2f,
                unitSize.vertical to unitSize.horizontal / 2f,
                -unitSize.horizontal to unitSize.vertical / 2f,
                unitSize.horizontal to unitSize.vertical / 2f
        )) {
            val p0 = centre + dp * (i + 0.5f)
            graphics.line(p0 + ds, p0 - ds, closeColour, settings.thickness)
        }
    }
}

fun GraphicsContext.drawGridOffsets(
        rectangle: Rectangle,
        drawOffset: (GridOffset, Rectangle) -> Unit
) {
    val maxHDistance = Grid.COLUMNS - 1f
    val maxVDistance = Grid.ROWS - 1f
    val unitSize = rectangle.size / Size(maxHDistance * 2 + 1, maxVDistance * 2 + 1)
    val centreRect = rectangle.resize(unitSize)
    val x = Grid.COLUMNS - 1
    val y = Grid.ROWS - 1

    for (dy in -y .. y) {
        for (dx in -x .. x) {
            val rect = centreRect.translateBy(unitSize.width * dx, -unitSize.height * dy)
            drawOffset(GridOffset(dx, dy), rect)
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

data class DrawGridSettings(
        val minorLineColour: Colour = Colour.lighterGrey,
        val majorLineColour: Colour = Colour.grey,
        val minorLineThickness: Float = 1f,
        val majorLineThickness: Float = 2f
) {
    companion object {
        val DEFAULT = DrawGridSettings()
    }
}

data class DrawGridOffsetSettings(
        val primaryColour: Colour = Colour.grey,
        val fadeColour: Colour = Colour.white,
        val thickness: Float = 2f
) {
    companion object {
        val DEFAULT = DrawGridOffsetSettings()
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

private infix fun <A, B, C> Pair<A, B>.to(third: C) = Triple(first, second, third)
