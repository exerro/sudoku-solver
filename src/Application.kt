import com.exerro.glfw.Window
import com.exerro.glfw.WindowProperty.*
import com.exerro.glfw.get
import com.exerro.glfw.gl.GLContext
import com.exerro.glfw.setHandler
import framework.*
import sudoku.Grid
import sudoku.drawGrid
import java.lang.Math.pow
import kotlin.math.min
import kotlin.math.pow

class Application(
        window: Window.Default,
        glc: GLContext,
        graphics: QueuedGraphicsContext
): BaseApplication(window, glc, graphics) {
    //////////////////////////////////////////////////////////////////////////////////////
    val grid = Grid.EMPTY.mapLocations { it }

    fun draw() {
        val screenSize = window[FRAMEBUFFER_SIZE].size
        val size = Size(min(screenSize.width, screenSize.height) - 100f)
        val offset = Position.origin + (screenSize - size) / 2f
        val rect = Rectangle(offset, size)

        graphics.clear(Colour.white)
        graphics.drawGrid(grid, rect) { item, area ->
            if (item.abs % 7 != 2 && item.abs % 6 != 0) {
                val textArea = Rectangle(
                        area.position + area.size.vertical * 0.3f,
                        area.size * Size(1f, 0.4f)
                )
                graphics.write("${item.row},${item.col}", textArea, Colour.blue)
            }
            else {
                val textArea = Rectangle(
                        area.position + area.size.vertical * 0.15f,
                        area.size * Size(1f, 0.8f)
                )
                graphics.write(item.abs.toString().takeLast(1), textArea, Colour.grey)
            }
        }
    }

    override fun initialise() {
        draw()
    }

    override fun update() {
        draw()
    }

    //////////////////////////////////////////////////////////////////////////////////////
}

private fun Float.pretty() = (this * 100).toInt() / 100f
