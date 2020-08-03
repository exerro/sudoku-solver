import com.exerro.glfw.Window

class Application(
        window: Window,
        graphics: GraphicsContext
): BaseApplication(window, graphics) {
    override fun initialise() {

    }

    override fun update() {
        graphics.clear(Colour.blue)
        graphics.rectangle(Rectangle(Position(20f, 0f), Size(100f, 100f)), Colour.white)
    }
}
