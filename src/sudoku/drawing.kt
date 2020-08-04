package sudoku

import framework.*

fun <Item> GraphicsContext.drawGrid(
        grid: Grid<Item>,
        rectangle: Rectangle,
        settings: DrawGridSettings = DrawGridSettings.DEFAULT,
        drawItem: (Item, Rectangle) -> Unit
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

    for (row in 0 .. 8) {
        for (col in 0 .. 8) {
            val size = rectangle.size / 9f
            val offset = rectangle.position + size * Size(col.toFloat(), row.toFloat())
            drawItem(grid[GridLocation.fromRowAndColumn(row, col)], Rectangle(offset, size))
        }
    }
}

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