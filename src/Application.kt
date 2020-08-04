import com.exerro.glfw.Window
import com.exerro.glfw.WindowProperty.*
import com.exerro.glfw.get
import com.exerro.glfw.gl.GLContext
import com.exerro.glfw.setHandler
import framework.*
import java.lang.Math.pow
import kotlin.math.pow

class Application(
        window: Window.Default,
        glc: GLContext,
        graphics: QueuedGraphicsContext
): BaseApplication(window, glc, graphics) {
    //////////////////////////////////////////////////////////////////////////////////////
    var thickness = 1f

    override fun initialise() {
        window.setHandler(SCROLL_CALLBACK) { _, dy ->
            thickness += dy.toFloat()
        }
    }

    override fun update() {
        graphics.clear(Colour.blue)

        var y = 0f

        setOf(14f, 16f, 20f, 24f, 28f, 32f, 48f, 52f, 64f, 72f, 92f, 108f, 128f, 256f).forEach { size ->
            graphics.write(
                    text = "$size: Hello world",
                    rectangle = Rectangle(0f, y, 0f, size),
                    colour = Colour.white,
                    alignment = Alignment.Left
            )
            y += size
        }
        val cp = window[CURSOR_POSITION]
        val sz = window[FRAMEBUFFER_SIZE]
        val min = cp.x.toFloat() / sz.width
        val max = cp.y.toFloat() / sz.height

        graphics.write(
                text = "${min.pretty()}, ${(min + (1 - min) * max).pretty()}",
                rectangle = Rectangle(sz.width - 200f, sz.height - 64f, 200f, 64f),
                colour = Colour.white,
                alignment = Alignment.Right
        )

        graphics.line(
                Position(100f),
                window[CURSOR_POSITION].let { (x, y) -> Position(x.toFloat(), y.toFloat()) },
                Colour.red,
                thickness,
                Colour.green
        )

        graphics.rectangle(Rectangle(95f, 95f, 10f, 10f), Colour.purple)
    }

    //////////////////////////////////////////////////////////////////////////////////////
}

private fun Float.pretty() = (this * 100).toInt() / 100f
