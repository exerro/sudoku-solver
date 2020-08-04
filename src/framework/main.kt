package framework

import Application
import com.exerro.glfw.GLFWInstance

fun main() {
    val instance = GLFWInstance.createInitialised()
    val (window, glc) = BaseApplication.createWindowAndContext()
    val graphics = BaseApplication.createGraphics(window)
    val app = Application(window, glc, graphics)

    app.initialise()

    BaseApplication.mainLoop(instance, app)
}
