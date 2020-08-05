import com.exerro.glfw.Window
import com.exerro.glfw.gl.GLContext
import framework.*
import framework.internal.QueuedGraphicsContext
import sudoku.Grid
import sudoku.drawGrid

class Application internal constructor(
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

        // clear the screen to be white, then draw the grid
        graphics.clear(Colour.white)
        graphics.drawGrid(grid, rect) { item, area, location ->
            // write non-blank items' values in the area given
            if (item != null) graphics.write(
                    item.toString(),
                    area.resizeVerticalBy(0.8f).translateVerticalBy(0.05f),
                    Colour.darkGrey
            )
            graphics.write(
                    "b${location.box}",
                    area.resizeVerticalBy(0.2f, 1f).resizeHorizontalBy(0.9f),
                    Colour.lightGrey,
                    Alignment.Left
            )
            graphics.write(
                    "i${location.indexInBox}",
                    area.resizeVerticalBy(0.2f, 1f).resizeHorizontalBy(0.9f),
                    Colour.lightGrey,
                    Alignment.Right
            )
        }

        // draw greyscale colours (shades of grey)
        Rectangle(Position.origin, Size(100f, windowSize.height))
                .splitVertical(Colours.greyscale.size - 1)
                .zip(Colours.greyscale - Colour.white)
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
