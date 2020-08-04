package framework

import Application
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
    /** Called once before the application starts. */
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

    companion object {
        const val REFRESH_RATE = 60
        const val UPDATE_DELAY = 1000L / REFRESH_RATE
        const val WORKER_TIMEOUT = 1000L

        /** Create a centred window and an OpenGL context. */
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

        /** Create a graphics context for the window given. */
        fun createGraphics(window: Window): QueuedGraphicsContext {
            GLFW.glfwMakeContextCurrent(window.glfwID)
            GL.createCapabilities()

            return QueuedGraphicsContext(window).also {
                GLFW.glfwMakeContextCurrent(MemoryUtil.NULL)
            }
        }

        /** Start background threads for various tasks of the application. */
        fun startBackgroundThreads(application: BaseApplication) {
            // THREAD: work
            // start a background worker thread, taking work items from [workQueue]
            // and running them in-order
            thread(start = true, isDaemon = true) {
                while (application.window.valid) {
                    val (name, fn) = application.workQueue.take()
                    application.workStartTime = System.currentTimeMillis()
                    application.currentWork = name
                    fn()
                    application.currentWork = null
                }
            }

            // THREAD: work monitor
            // start a background thread watching for worker tasks taking more than
            // [WORKER_TIMEOUT] to run; if one is found, print a warning message
            thread(start = true, isDaemon = true) {
                while (application.window.valid) when (val work = application.currentWork) {
                    null -> Thread.sleep(WORKER_TIMEOUT)
                    else -> {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - application.workStartTime > WORKER_TIMEOUT) {
                            println("\u001b[33mWarning: '$work' has been running for over 1 second!\u001b[0m")
                            application.currentWork = null
                        }
                        else {
                            Thread.sleep(application.workStartTime + WORKER_TIMEOUT - currentTime)
                        }
                    }
                }
            }

            // THREAD: render
            // start a background render thread to render changes
            thread(start = true, isDaemon = true) {
                GLFW.glfwMakeContextCurrent(application.window.glfwID)
                GL.createCapabilities()

                while (application.window.valid) {
                    if (application.internalGraphics.isDirty()) {
                        application.internalGraphics.renderAll()
                        application.glc.swapBuffers()
                    }
                    else Thread.sleep(UPDATE_DELAY)
                }
            }
        }

        /** Set window callbacks mapping to the application's callbacks. */
        fun setApplicationCallbacks(application: BaseApplication) {
            application.window.setHandler(DAMAGED) {
                application.internalGraphics.makeDirty()
                application.redraw()
            }
        }

        /** Run the application's main loop. */
        fun mainLoop(instance: GLFWInstance, application: BaseApplication) {
            application.window[VISIBLE] = true

            val (benArea, descArea) = Rectangle(Position.origin, application.windowSize)
                    .resizeVertical(128f)
                    .splitVertical(0.7f)

            fun draw(alpha: Float) {
                application.internalGraphics.begin()
                application.internalGraphics.clear(Colour.white)
                application.internalGraphics.write("Ben is awesome", benArea, Colour.cyan.copy(alpha = alpha))
                application.internalGraphics.write("Ben's Sudoku Engine", descArea, Colour.lightGrey.copy(alpha = alpha))
                application.internalGraphics.finish()
            }

            (0 .. 100).forEach {
                draw(it / 100f)
                Thread.sleep(2)
                instance.pollEvents()
            }
            (0 .. 100).forEach {
                draw(1f)
                Thread.sleep(20)
                instance.pollEvents()
            }
            (0 .. 100).forEach {
                draw(1 - it / 100f)
                Thread.sleep(5)
                instance.pollEvents()
            }
            application.internalGraphics.clear(Colour.white)
            Thread.sleep(500)
            application.redraw()

            var needsToUpdate = true

            while (!application.window[SHOULD_CLOSE]) {
                instance.pollEvents()

                if (needsToUpdate) {
                    needsToUpdate = false
                    application.submitWork("Application.update()") {
                        application.update()
                        needsToUpdate = true
                    }
                }
            }

            application.window.destroy()
            instance.terminate()
        }
    }
}

fun main() {
    val instance = GLFWInstance.createInitialised()
    val (window, glc) = BaseApplication.createWindowAndContext()
    val graphics = BaseApplication.createGraphics(window)
    val app = Application(window, glc, graphics)

    app.initialise()

    BaseApplication.startBackgroundThreads(app)
    BaseApplication.setApplicationCallbacks(app)
    BaseApplication.mainLoop(instance, app)
}
