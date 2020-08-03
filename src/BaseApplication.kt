import com.exerro.glfw.Window

abstract class BaseApplication(
        val window: Window,
        val graphics: GraphicsContext
) {
    abstract fun initialise()
    abstract fun update()
}
