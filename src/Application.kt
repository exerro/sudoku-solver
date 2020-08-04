import com.exerro.glfw.Window
import com.exerro.glfw.gl.GLContext
import framework.*
import sudoku.Grid
import sudoku.drawGrid

class Application(
        window: Window.Default,
        glc: GLContext,
        graphics: QueuedGraphicsContext
): BaseApplication(window, glc, graphics) {
    //////////////////////////////////////////////////////////////////////////////////////
    val grid = Grid.EMPTY.mapLocations { it }

    override fun redraw() {
        val rect = Rectangle(Position.origin, windowSize)
                .minSquare()
                .resizeBy(0.9f)

        val colours = listOf(
                Colour.black,
                Colour.charcoal,
                Colour.darkGrey,
                Colour.grey,
                Colour.lighterGrey,
                Colour.lightGrey,
                Colour.ultraLightGrey
        )

        graphics.clear(Colour.white)
        graphics.drawGrid(grid, rect) { item, area ->
            if (item.abs % 7 != 2 && item.abs % 6 != 0) {
                val textArea = area.resizeVertical(area.size.height * 0.4f)
                graphics.write("${item.row},${item.col}", textArea, Colour.blue)
            }
            else {
                val textArea = area
                        .resizeVertical(area.size.height * 0.8f)
                        .translateVertical(area.size.height * 0.05f)
                graphics.write(item.abs.toString().takeLast(1), textArea, Colour.grey)
            }
        }

        Rectangle(Position.origin, Size(100f, windowSize.height))
                .splitVertical(colours.size)
                .zip(colours)
                .forEach { (rect, colour) -> graphics.rectangle(rect, colour) }
    }

    override fun initialise() {

    }

    override fun update() {

    }

    //////////////////////////////////////////////////////////////////////////////////////
}

private fun Float.pretty() = (this * 100).toInt() / 100f
