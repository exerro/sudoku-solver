import com.exerro.glfw.GLFWInstance
import com.exerro.glfw.Window
import com.exerro.glfw.WindowProperty.SHOULD_CLOSE
import com.exerro.glfw.WindowProperty.TITLE
import com.exerro.glfw.WindowSettings
import com.exerro.glfw.get
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil

fun main() {
    val instance = GLFWInstance.createInitialised()
    val (window, glc) = WindowSettings.default
            .set(TITLE, "Sudoku Solver")
            .createWindowWithGL()
            ?: error("Failed to create window.")

    glfwMakeContextCurrent(window.glfwID)
    GL.createCapabilities()

    val graphics = QueuedGraphicsContext(window)
    val app = Application(graphics)

    app.initialise()

    while (!window[SHOULD_CLOSE]) {
        instance.pollEvents()
        app.update()
        graphics.renderQueue()
        glc.swapBuffers()
    }

    window.destroy()
    instance.terminate()
}
