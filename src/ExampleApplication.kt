import com.exerro.glfw.Window
import com.exerro.glfw.data.MouseModifier
import framework.*
import sudoku.*
import kotlin.math.abs

// Main is the first function to run.
fun main() {
    // Launch the application. The :: is a "method reference" - basically this
    // is saying "launch an application using this method reference
    // (::ExampleApplication) to create the application."
    BaseApplication.launch(::ExampleApplication)
}

/** Example application showing roughly how to create an application, respond to
 *  events, draw to the screen, and use the grid utilities provided. */
class ExampleApplication(
        // The constructor takes a window, a graphics context, and a GL context
        // and simply passes those to the constructor of "BaseApplication",
        // which is the class that ExampleApplication extends.
        // The window is a handle to the window being shown on the screen. It
        // can be used to do more advanced things like setting event callbacks,
        // setting size limits, querying whether keys are pressed etc.
        window: Window.Default,
        // The graphics context handles all drawing operations like drawing
        // rectangles and writing text. Most graphics operations use rectangles
        // or positions.
        graphics: GraphicsContext
): BaseApplication(window, graphics) {
    // Load a grid from the string GRID1 (at the bottom) and store this in the
    // field 'grid'.
    val grid = Grid.load(GRID1)
    /** Whether to draw a grid or not. */
    var drawGrid = true

    // Overrides the default 'redraw' function inherited from BaseApplication,
    // which is called for you automatically in response to certain events like
    // starting up or having the window resized.
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

    override fun mousePressed(position: Position, leftClick: Boolean, modifiers: Set<MouseModifier>) {
        drawGrid = !drawGrid
        redraw()
    }
}

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
