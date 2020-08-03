package framework

import Application
import com.exerro.glfw.GLFWInstance
import com.exerro.glfw.WindowProperty.*
import com.exerro.glfw.get
import com.exerro.glfw.set
import com.exerro.glfw.setHandler

fun main() {
    val instance = GLFWInstance.createInitialised()
    val (window, glc) = BaseApplication.createWindowAndContext()
    val graphics = BaseApplication.createGraphics(window)
    val app = Application(window, glc, graphics)

    app.initialise()

    window.setHandler(DAMAGED) { graphics.makeDirty() }
    window[VISIBLE] = true

    var needsToUpdate = true

    while (!window[SHOULD_CLOSE]) {
        instance.pollEvents()

        if (needsToUpdate) {
            needsToUpdate = false
            app.submitWork("Application.update()") {
                app.update()
                needsToUpdate = true
            }
        }
    }

    window.destroy()
    instance.terminate()
}
