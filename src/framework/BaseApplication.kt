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

abstract class BaseApplication internal constructor(
        /** TODO */
        val window: Window.Default,
        /** Graphics context which is handled by the application internally. Used
         *  for all drawing operations. */
        val graphics: GraphicsContext
) {
    /** Called once before the application starts. */
    open fun initialise() {}

    /** Called whenever application should redraw its entire contents, for
     *  example due to the window resizing or being damaged. Also called once
     *  after initialisation. */
    open fun redraw() {}

    /** Called repeatedly while the application is running. Note, the intervals
     *  between calls may vary. */
    open fun update() {}

    /** Called once when the mouse is pressed. */
    open fun mousePressed(position: Position, leftClick: Boolean, modifiers: Set<MouseModifier>) {}

    /** Called once when a key is pressed. */
    open fun keyPressed(key: Key, modifiers: Set<KeyModifier>) {}

    ////////////////////////////////////////////////////////////////////////////

    /** Size of the window in pixels. This may change if the window is resized. */
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
        const val WORK_TIMEOUT = 1000L

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

        /** Start a background worker thread, taking work items from [workQueue]
         *  and running them in-order. */
        private fun startApplicationWorkThread(
                application: BaseApplication
        ) {
            thread(start = true, isDaemon = true) {
                while (application.window.valid) {
                    val (name, fn) = application.workQueue.take()
                    application.workStartTime = System.currentTimeMillis()
                    application.currentWork = name
                    fn()
                    application.currentWork = null
                }
            }
        }

        /** Start a thread watching for worker tasks taking more than
         *  [WORK_TIMEOUT] to run; if one is found, print a warning message. */
        private fun startApplicationWorkMonitorThread(
                application: BaseApplication
        ) {
            thread(start = true, isDaemon = true) {
                while (application.window.valid) when (val work = application.currentWork) {
                    null -> Thread.sleep(WORK_TIMEOUT)
                    else -> {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - application.workStartTime > WORK_TIMEOUT) {
                            println("\u001b[33mWarning: '$work' has been running for over 1 second!\u001b[0m")
                            application.currentWork = null
                        }
                        else {
                            Thread.sleep(application.workStartTime + WORK_TIMEOUT - currentTime)
                        }
                    }
                }
            }
        }

        /** Start a thread to render changes to an application's graphics
         *  context and present them to the screen. */
        private fun startApplicationRenderThread(
                glc: GLContext,
                application: BaseApplication,
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
                application: BaseApplication,
                graphics: QueuedGraphicsContext
        ) {
            application.window.setHandler(DAMAGED) {
                graphics.makeDirty()
                application.redraw()
            }

            application.window.setHandler(KEY_CALLBACK) { key, pressed, modifiers, _ ->
                if (pressed) application.keyPressed(key, modifiers)
            }

            application.window.setHandler(MOUSE_BUTTON_CALLBACK) { button, pressed, modifiers ->
                val cursorPosition = application.window[CURSOR_POSITION]
                val position = Position(cursorPosition.x.toFloat(), cursorPosition.y.toFloat())
                if (pressed) application.mousePressed(position, button == MouseButton.LEFT, modifiers)
            }
        }

        /** Don't ask. */
        private fun drawStupidSplashScreen(
                instance: GLFWInstance,
                application: BaseApplication,
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

        /** Run the application's main loop. */
        private fun mainLoop(instance: GLFWInstance, application: BaseApplication) {
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

        fun <T: BaseApplication> launch(fn: (Window.Default, GraphicsContext) -> T) {
            val instance = GLFWInstance.createInitialised()
            val (window, glc) = createWindowAndContext()
            val graphics = createGraphics(window)
            val app = fn(window, graphics)

            app.initialise()

            startApplicationWorkThread(app)
            startApplicationWorkMonitorThread(app)
            startApplicationRenderThread(glc, app, graphics)
            setApplicationCallbacks(app, graphics)
            app.window[VISIBLE] = true
            drawStupidSplashScreen(instance, app, graphics)
            app.redraw()
            mainLoop(instance, app)
        }
    }
}
