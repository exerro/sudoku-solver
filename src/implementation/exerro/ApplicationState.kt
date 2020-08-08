package implementation.exerro

import com.exerro.glfw.data.Key
import com.exerro.glfw.data.KeyModifier
import framework.GraphicsContext

interface ApplicationState {
    val title: String
    val layout: ApplicationLayout
    val enableSidebar: Boolean get() = true

    fun draw(graphics: GraphicsContext)
    fun paste(text: String) {}
    fun keyPressed(key: Key, modifiers: Set<KeyModifier>) {}
    fun mousePressed(key: Key, modifiers: Set<KeyModifier>) {}
}
