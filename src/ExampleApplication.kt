import com.exerro.glfw.GLFWInstance
import com.exerro.glfw.Window
import com.exerro.glfw.data.MouseModifier
import framework.*
import framework.internal.QueuedGraphicsContext
import sudoku.*
import kotlin.math.abs

/*
This is an example application using the framework provided in this repository.
Package 'framework' contains general code related to graphics and windowing,
while package 'sudoku' contains utilities specific to sudoku.

The rough approach is to create a subclass of Application and then call
`Application.launch(::MyApplication)` which will handle all of the window
creation and graphics setup automagically.
Application has callbacks which may be overridden, such as handling input
events, drawing, and starting. Any application should frequently call
processEvents() and use the 'running' field to stop when necessary.
 */

// An example sudoku grid.
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

// Main is the first function to run.
fun main() {
    // Launch the application. The :: is a "method reference" - basically this
    // is saying "launch an application using this method reference
    // (::ExampleApplication) to create the application."
    // It will create an ExampleApplication, set everything up, and then run the
    // ExampleApplication that it created.
    Application.launch(::ExampleApplication)
}

/** Example application showing roughly how to create an application, respond to
 *  events, draw to the screen, and use the sudoku utilities provided. */
class ExampleApplication(
        // The constructor takes a window, a graphics context, and a GLFW
        // instance and simply passes those to the constructor of
        // `Application`, which is the class that ExampleApplication extends.
        // `window` is a handle to the window being shown on the screen. It
        // can be used to do more advanced things like setting event callbacks,
        // setting size limits, querying whether keys are held etc, but
        // shouldn't need to be used at all.
        window: Window.Default,
        // The graphics context handles all drawing operations like filling
        // rectangles and writing text. Most graphics operations use rectangles
        // or positions to determine where they are being drawn. These are
        // defined in `framework/geometry.kt`.
        graphics: QueuedGraphicsContext,
        // This just has to be given for stuff to work. It represents an
        // initialised GLFW system from which you can poll events and create
        // windows etc.
        instance: GLFWInstance
): Application(window, graphics, instance) {
    // Everything further within this example application is optional and can be
    // removed while still handling the window creation and closing properly.
    // `start()` has a default implementation in Application equal to the
    // one here.

    // Load a grid from the string GRID1 (at the bottom) and store this in the
    // field 'grid'.
    val grid = Grid.load(GRID1)
    /** Whether to draw a grid or not. */
    var drawGrid = true

    // Start is called when the application is launched, and is responsible for
    // running all the code in the application. This draws initially to get
    // stuff on the screen, and then processes events in a loop. See the
    // documentation for draw() and processEvents()
    override fun start() {
        draw()
        while (running) processEvents()
    }

    // MousePressed is called whenever the user clicks within the window.
    override fun mousePressed(position: Position, leftClick: Boolean, modifiers: Set<MouseModifier>) {
        drawGrid = !drawGrid
        draw()
    }

    // Overrides the default 'redraw' function inherited from Application,
    // which is called for you automatically in response to certain events like
    // starting up or having the window resized.
    override fun draw() {
        val sudokuArea = Rectangle(Position.origin, windowSize)
                .minSquare()
                .resizeBy(0.9f)

        graphics.begin()
        graphics.clear(Colour.white)

        if (drawGrid) {
            graphics.drawGridlines(sudokuArea)
            graphics.drawGrid(sudokuArea) { area, location ->
                val item = grid[location]
                // write non-blank items' values in the area given
                if (item != null) graphics.write(
                        item.toString(),
                        area.resizeVerticalBy(0.8f).translateVerticalRelative(0.05f),
                        Colour.darkGrey
                )
            }
        }
        else {
            graphics.drawGridOffsets(sudokuArea) { offset, area -> when {
                offset == GridOffset.NONE -> graphics.rectangle(area, Colour.cyan)
                offset.right * offset.up == 0 -> graphics.rectangle(area, Colour.yellow)
                offset.right * offset.up == 1 -> graphics.rectangle(area, Colour.orange)
                offset.right * offset.up == -1 -> graphics.rectangle(area, Colour.orange)
                abs(offset.right) + abs(offset.up) == 3 -> graphics.rectangle(area, Colour.red)
            } }
            graphics.drawGridOffsetLines(sudokuArea)
        }

        graphics.finish()
    }
}
