import com.exerro.glfw.Window
import com.exerro.glfw.gl.GLContext
import framework.*

class Application(
        window: Window.Default,
        glc: GLContext,
        graphics: QueuedGraphicsContext
): BaseApplication(window, glc, graphics) {
    //////////////////////////////////////////////////////////////////////////////////////

    override fun initialise() {

    }

    override fun update() {
        println("Updating")
        Thread.sleep(1100)
        graphics.clear(Colour.blue)
        graphics.rectangle(Rectangle(Position(20f, 0f), Size(100f, 100f)), Colour.white)
    }

    //////////////////////////////////////////////////////////////////////////////////////
}
