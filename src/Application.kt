import org.lwjgl.glfw.GLFW.glfwGetTime

class Application(
        val graphics: GraphicsContext
) {
    val colours = listOf(
            Colour.red,
            Colour.green,
            Colour.blue,
            Colour.yellow,
            Colour.pink,
            Colour.purple,
            Colour.cyan,
            Colour.black,
            Colour.charcoal,
            Colour.darkGrey,
            Colour.grey,
            Colour.lightGrey,
            Colour.white
    )
    var index = 0

    fun initialise() {

    }

    fun update() {
        graphics.clear(colours[index])
        graphics.rectangle(Rectangle(Position(20f, 0f), Size(100f, 100f)), Colour.white)
        index = glfwGetTime().toInt() % colours.size
    }
}
