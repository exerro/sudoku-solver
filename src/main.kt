import com.exerro.glfw.*
import com.exerro.glfw.WindowProperty.*
import com.exerro.glfw.data.WindowPosition
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.opengl.GL

fun main() {
    val instance = GLFWInstance.createInitialised()
    val (window, glc) = WindowSettings.default
            .set(TITLE, "Sudoku Solver")
            .set(VISIBLE, false)
            .set(FOCUSED, true)
            .createWindowWithGL()
            ?: error("Failed to create window.")
    val monitorWidth = Monitor.primaryMonitor().videoMode().mode.width()
    val monitorHeight = Monitor.primaryMonitor().videoMode().mode.height()
    val size = window[SIZE]

    window[POSITION] = WindowPosition(
            monitorWidth / 2 - size.width / 2,
            monitorHeight / 2 - size.height / 2
    )

    glfwMakeContextCurrent(window.glfwID)
    GL.createCapabilities()

    val graphics = QueuedGraphicsContext(window)
    val app = Application(window, graphics)

    app.initialise()
    window[VISIBLE] = true
    window.setHandler(DAMAGED) { graphics.makeDirty() }

    while (!window[SHOULD_CLOSE]) {
        instance.pollEvents()
        app.update()
        if (graphics.renderChanges()) glc.swapBuffers()
    }

    window.destroy()
    instance.terminate()
}
