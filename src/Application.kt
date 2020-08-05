import com.exerro.glfw.Window
import com.exerro.glfw.data.Key
import com.exerro.glfw.data.KeyModifier
import com.exerro.glfw.data.MouseModifier
import com.exerro.glfw.gl.GLContext
import framework.*
import framework.internal.QueuedGraphicsContext
import sudoku.*
import kotlin.math.abs

class Application internal constructor(
        window: Window.Default,
        glc: GLContext,
        graphics: QueuedGraphicsContext
): BaseApplication(window, glc, graphics) {
    //////////////////////////////////////////////////////////////////////////////////////
    val grid = Grid.load(GRID1)
    var drawGrid = true

    override fun redraw() {
        val rect = Rectangle(Position.origin, windowSize)
                .minSquare()
                .resizeBy(0.9f)

        graphics.begin()
        graphics.clear(Colour.white)

        if (drawGrid) {
            graphics.drawGridlines(rect)
            graphics.drawGrid(grid, rect) { item, area, _ ->
                // write non-blank items' values in the area given
                if (item != null) graphics.write(
                        item.toString(),
                        area.resizeVerticalBy(0.8f).translateVerticalRelative(0.05f),
                        Colour.darkGrey
                )
            }
        }
        else {
            graphics.drawGridOffsets(rect) { offset, area -> when {
                offset == GridOffset.NONE -> graphics.rectangle(area, Colour.cyan)
                offset.right * offset.up == 0 -> graphics.rectangle(area, Colour.yellow)
                offset.right * offset.up == 1 -> graphics.rectangle(area, Colour.orange)
                offset.right * offset.up == -1 -> graphics.rectangle(area, Colour.orange)
                abs(offset.right) + abs(offset.up) == 3 -> graphics.rectangle(area, Colour.red)
            } }
            graphics.drawGridOffsetLines(rect)
        }

        graphics.finish()
    }

    override fun initialise() {

    }

    override fun update() {

    }

    override fun mousePressed(position: Position, leftClick: Boolean, modifiers: Set<MouseModifier>) {
        drawGrid = !drawGrid
        redraw()
    }

    override fun keyPressed(key: Key, modifiers: Set<KeyModifier>) {

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
