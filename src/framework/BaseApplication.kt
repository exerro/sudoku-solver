package framework

import com.exerro.glfw.*
import com.exerro.glfw.WindowProperty.*
import com.exerro.glfw.data.WindowPosition
import com.exerro.glfw.gl.GLContext
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_SAMPLES
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.concurrent.thread

abstract class BaseApplication(
        val window: Window.Default,
        private val glc: GLContext,
        private val internalGraphics: QueuedGraphicsContext
) {
    /** Called once when the application starts. */
    abstract fun initialise()

    /** Called whenever application should redraw its entire contents, for
     *  example due to the window resizing or being damaged. Also called once
     *  after initialisation. */
    abstract fun redraw()

    /** Called repeatedly while the application is running. Note, the intervals
     *  between calls may vary. */
    abstract fun update()

    ////////////////////////////////////////////////////////////////////////////

    /** Graphics context which is handled by the application internally. Used
     *  for all drawing operations. */
    val graphics: GraphicsContext = internalGraphics

    val windowSize: Size get() = window[FRAMEBUFFER_SIZE].let { (w, h) -> Size(w.toFloat(), h.toFloat()) }

    /** Run a named function. */
    fun submitWork(name: String, fn: () -> Unit) {
        workQueue.put(name to fn)
    }

    //////////////////////////////////////////////////////////////////////////////////////

    private val workQueue: BlockingQueue<Pair<String, () -> Unit>> = ArrayBlockingQueue(1024)
    private var currentWork: String? = null
    private var workStartTime: Long = 0L

    init {
        internalGraphics.clear(Colour.white)
        internalGraphics.renderAll()
        glc.swapBuffers()

        // start a background worker thread, taking work items from [workQueue]
        // and running them in-order
        thread(start = true, isDaemon = true) {
            while (window.valid) {
                val (name, fn) = workQueue.take()
                workStartTime = System.currentTimeMillis()
                currentWork = name
                fn()
                currentWork = null
            }
        }

        // start a background thread watching for worker tasks taking more than
        // [WORKER_TIMEOUT] to run; if one is found, print a warning message
        thread(start = true, isDaemon = true) {
            while (window.valid) when (val work = currentWork) {
                null -> Thread.sleep(WORKER_TIMEOUT)
                else -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - workStartTime > WORKER_TIMEOUT) {
                        println("\u001b[33mWarning: '$work' has been running for over 1 second!\u001b[0m")
                        currentWork = null
                    }
                    else {
                        Thread.sleep(workStartTime + WORKER_TIMEOUT - currentTime)
                    }
                }
            }
        }

        // start a background render thread to render changes
        thread(start = true, isDaemon = true) {
            GLFW.glfwMakeContextCurrent(window.glfwID)
            GL.createCapabilities()

            while (window.valid) {
                if (internalGraphics.isDirty()) {
                    internalGraphics.renderAll()
                    glc.swapBuffers()
                }
                else Thread.sleep(UPDATE_DELAY)
            }
        }

        window.setHandler(DAMAGED) {
            internalGraphics.begin()
            internalGraphics.makeDirty()
            redraw()
            internalGraphics.finish()
        }
    }

    companion object {
        const val REFRESH_RATE = 60
        const val UPDATE_DELAY = 1000L / REFRESH_RATE
        const val WORKER_TIMEOUT = 1000L

        fun createWindowAndContext(): Pair<Window.Default, GLContext> {
            glfwWindowHint(GLFW_SAMPLES, 4)

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

            return window to glc
        }

        fun createGraphics(window: Window): QueuedGraphicsContext {
            GLFW.glfwMakeContextCurrent(window.glfwID)
            GL.createCapabilities()

            val graphics = QueuedGraphicsContext(window)

            GLFW.glfwMakeContextCurrent(MemoryUtil.NULL)

            return graphics
        }

        fun mainLoop(instance: GLFWInstance, application: BaseApplication) {
            application.window[VISIBLE] = true

            var needsToUpdate = true

            while (!application.window[SHOULD_CLOSE]) {
                instance.pollEvents()

                if (needsToUpdate) {
                    needsToUpdate = false
                    application.submitWork("Application.update()") {
                        application.internalGraphics.begin()
                        application.update()
                        application.internalGraphics.finish()
                        needsToUpdate = true
                    }
                }
            }

            application.window.destroy()
            instance.terminate()
        }
    }
}
