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
    val grid = Grid.load(GRID1)

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
            if (item != null) {
                graphics.write(
                        item.toString(),
                        area.resizeVerticalBy(0.8f).translateVerticalBy(0.05f),
                        Colour.darkGrey
                )
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

const val GRID1 = """
0 4 0 0 0 0 1 7 9 
0 0 2 0 0 8 0 5 4 
0 0 6 0 0 5 0 0 8 
0 8 0 0 7 0 9 1 0 
0 5 0 0 9 0 0 3 0 
0 1 9 0 6 0 0 4 0 
3 0 0 4 0 0 7 0 0 
5 7 0 1 0 0 2 0 0 
9 2 8 0 0 0 0 6 0
"""
