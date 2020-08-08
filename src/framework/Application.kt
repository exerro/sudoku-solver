package framework

import com.exerro.glfw.*
import com.exerro.glfw.WindowProperty.*
import com.exerro.glfw.data.*
import com.exerro.glfw.gl.GLContext
import framework.internal.QueuedGraphicsContext
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_SAMPLES
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.concurrent.thread

// TODO: add monitor check for not calling processEvents()

abstract class Application internal constructor(
        /** The window in which the application is drawing stuff. */
        val window: Window.Default,
        /** Graphics context which is handled by the application internally. Used
         *  for all drawing operations. */
        val graphics: GraphicsContext,
        private val instance: GLFWInstance
) {
    /** Called once before the application starts and before the window is
     *  created. */
    open fun setup() {}

    /** Called when the application is ready to start. */
    open fun start() {
        draw()
        while (running) processEvents()
    }

    /** Responsible for drawing the entire contents of the screen. Is called
     *  automatically whenever application should redraw its entire contents,
     *  for example due to the window being resized or damaged. Can also be
     *  called from your own code whenever. */
    open fun draw() {}

    /** Called when the mouse is pressed. */
    open fun mousePressed(position: Position, leftClick: Boolean, modifiers: Set<MouseModifier>) {}

    /** Called when a key is pressed. */
    open fun keyPressed(key: Key, modifiers: Set<KeyModifier>) {}

    /** Called when the window is resized. */
    open fun windowResized() {}

    /** Called when one or more files are dragged onto the window. */
    open fun pathsDropped(paths: List<String>) {}

    /** Whether to spawn a thread that looks out for potential issues. */
    open val spawnMonitor: Boolean = true

    ////////////////////////////////////////////////////////////////////////////

    /** Size of the window in pixels. This may change if the window is resized. */
    val windowSize: Size get() =
        window[FRAMEBUFFER_SIZE].let { (w, h) -> Size(w.toFloat(), h.toFloat()) }

    /** Position of the mouse cursor within the window. */
    val cursorPosition: Position get() =
        window[CURSOR_POSITION].let { (x, y) -> Position(x.toFloat(), y.toFloat()) }

    /** Whether the application is running. */
    var running: Boolean = true
        private set

    /** Run a named function in the background. */
    fun background(name: String = "", fn: () -> Unit) {
        taskQueue.put(name to fn)
    }

    /** Stop the application from running. */
    fun stop() {
        running = false
    }

    /** Process events. Must be called for the [mousePressed] and [keyPressed]
     *  callbacks to work, and should be called regularly. If a task will take
     *  a long time to run, consider running it in the [background]. */
    fun processEvents() {
        instance.pollEvents()
    }

    //////////////////////////////////////////////////////////////////////////////////////

    private val taskQueue: BlockingQueue<Pair<String, () -> Unit>> = ArrayBlockingQueue(1024)
    private var currentTask: String? = null
    private var taskStartTime: Long = 0L

    companion object {
        const val REFRESH_RATE = 60
        const val UPDATE_DELAY = 1000L / REFRESH_RATE
        const val TASK_TIMEOUT = 1000L

        /** Create a centred window and an OpenGL context. */
        private fun createWindowAndContext(): Pair<Window.Default, GLContext> {
            glfwWindowHint(GLFW_SAMPLES, 4)

            val (window, glc) = WindowSettings.default
                    .set(TITLE, "Sudoku Solver")
                    .set(SIZE, WindowSize(1720, 960))
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
        private fun createGraphics(window: Window): QueuedGraphicsContext {
            GLFW.glfwMakeContextCurrent(window.glfwID)
            GL.createCapabilities()

            return QueuedGraphicsContext(window).also {
                GLFW.glfwMakeContextCurrent(MemoryUtil.NULL)
            }
        }

        /** Start a background task thread, taking tasks from [taskQueue] and
         *  running them in-order. */
        private fun startApplicationTaskThread(
                application: Application
        ) {
            thread(start = true, isDaemon = true) {
                while (application.window.valid) {
                    val (name, fn) = application.taskQueue.take()
                    application.taskStartTime = System.currentTimeMillis()
                    application.currentTask = name
                    fn()
                    application.currentTask = null
                }
            }
        }

        /** Start a thread watching for tasks taking more than [TASK_TIMEOUT] to
         *  run; if one is found, print a warning message. */
        private fun startApplicationMonitorThread(
                application: Application
        ) {
            thread(start = true, isDaemon = true) {
                while (application.window.valid) when (val work = application.currentTask) {
                    null -> Thread.sleep(TASK_TIMEOUT)
                    else -> {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - application.taskStartTime > TASK_TIMEOUT) {
                            println("\u001b[33mWarning: '$work' has been running for over 1 second!\u001b[0m")
                            application.currentTask = null
                        }
                        else {
                            Thread.sleep(application.taskStartTime + TASK_TIMEOUT - currentTime)
                        }
                    }
                }
            }
        }

        /** Start a thread to render changes to an application's graphics
         *  context and present them to the screen. */
        private fun startApplicationRenderThread(
                glc: GLContext,
                application: Application,
                graphics: QueuedGraphicsContext
        ) {
            thread(start = true, isDaemon = true) {
                GLFW.glfwMakeContextCurrent(application.window.glfwID)
                GL.createCapabilities()

                while (application.window.valid) {
                    if (graphics.isDirty()) {
                        graphics.renderAll()
                        glc.swapBuffers()
                    }
                    else Thread.sleep(UPDATE_DELAY)
                }
            }
        }

        /** Set window callbacks mapping to the application's callbacks. */
        private fun setApplicationCallbacks(
                application: Application,
                graphics: QueuedGraphicsContext
        ) {
            application.window.setHandler(DAMAGED) {
                graphics.makeDirty()
                application.draw()
            }

            application.window.setHandler(FRAMEBUFFER_SIZE) {
                application.windowResized()
            }

            application.window.setHandler(PATH_DROP_CALLBACK) {
                application.pathsDropped(it)
            }

            application.window.setHandler(KEY_CALLBACK) { key, pressed, modifiers, _ ->
                if (pressed) application.keyPressed(key, modifiers)
            }

            application.window.setHandler(MOUSE_BUTTON_CALLBACK) { button, pressed, modifiers ->
                val cursorPosition = application.window[CURSOR_POSITION]
                val position = Position(cursorPosition.x.toFloat(), cursorPosition.y.toFloat())
                if (pressed) application.mousePressed(position, button == MouseButton.LEFT, modifiers)
            }

            application.window.setHandler(SHOULD_CLOSE) {
                application.running = false
            }
        }

        /** Don't ask. */
        private fun drawStupidSplashScreen(
                instance: GLFWInstance,
                application: Application,
                graphics: GraphicsContext
        ) {
            val (benArea, descArea) = Rectangle(Position.origin, application.windowSize)
                    .resizeVertical(128f)
                    .splitVertical(0.7f)

            fun draw(alpha: Float) {
                graphics.begin()
                graphics.clear(Colour.white)
                graphics.write("Ben is awesome", benArea, Colour.cyan.withAlpha(alpha))
                graphics.write("Ben's Sudoku Engine", descArea, Colour.lightGrey.withAlpha(alpha))
                graphics.finish()
                Thread.sleep(5)
                instance.pollEvents()
            }

            repeat(100) { draw(it / 100f) }
            repeat(100) { draw(1f) }
            repeat(100) { draw(1 - it / 100f) }
            graphics.clear(Colour.white)
            Thread.sleep(500)
        }

        fun <T: Application> launch(
                fn: (Window.Default, GraphicsContext, GLFWInstance) -> T
        ) {
            val instance = GLFWInstance.createInitialised()
            val (window, glc) = createWindowAndContext()
            val graphics = createGraphics(window)
            val app = fn(window, graphics, instance)

            app.setup()

            startApplicationTaskThread(app)
            if (app.spawnMonitor) startApplicationMonitorThread(app)
            startApplicationRenderThread(glc, app, graphics)
            setApplicationCallbacks(app, graphics)
            app.window[VISIBLE] = true
            // drawStupidSplashScreen(instance, app, graphics)
            app.start()
            window.destroy()
            instance.terminate()
        }
    }
}
