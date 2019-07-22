import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.graphics.Font
import ktaf.graphics.rectangle
import ktaf.typeclass.plus
import ktaf.ui.Hotkey
import ktaf.ui.elements.UIButton
import ktaf.ui.layout.GridLayout
import ktaf.ui.node.UIContainer
import org.lwjgl.glfw.GLFW

class SudokuNodeDisplay(node: KTAFValue<Int>) : UIContainer() {
    private val label = children.add(UIButton("")) {}

    init {
        node.connect { when (it) {
            0 -> label.text("")
            else -> label.text(it.toString())
        } }

        label.colour(rgba(0.3f, 0.6f, 0.9f, 0f))
        label.textColour(rgba(0.1f))
        label.font(Font.DEFAULT_FONT.scaleTo(28f))

        label.focused.connect { when (it) {
            true -> label.colour(rgba(0.3f, 0.6f, 0.9f, 0.4f))
            false -> label.colour(rgba(0.3f, 0.6f, 0.9f, 0f))
        } }

        (1 .. 9).forEach {
            label.hotkeys.add(Hotkey(GLFW.GLFW_KEY_0 + it))
            label.onKeyPress { e ->
                if (!focused.get()) return@onKeyPress
                val n = e.key - GLFW.GLFW_KEY_0
                if (n in 1 .. 9) { node(n) }
            }
        }
    }
}

class SudokuGridDisplay(grid: SudokuGrid = SudokuGrid()): UIContainer() {
    private val nodes = KTAFList<SudokuNodeDisplay>()

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        super.draw(context, position, size)

        context.draw {
            context.colour = colour.get().darken()
            listOf(1, 2, 4, 5, 7, 8).forEach { n ->
                rectangle(position + vec2(n * size.x / 9f - 1.5f, 0f), vec2(3f, size.y))
                rectangle(position + vec2(0f, n * size.y / 9f - 1.5f), vec2(size.x, 3f))
            }

            context.colour = colour.get().darken().darken()
            rectangle(position + vec2(1 * size.x / 3f - 1.5f, 0f), vec2(3f, size.y))
            rectangle(position + vec2(2 * size.x / 3f - 1.5f, 0f), vec2(3f, size.y))
            rectangle(position + vec2(0f, 1 * size.y / 3f - 1.5f), vec2(size.x, 3f))
            rectangle(position + vec2(0f, 2 * size.y / 3f - 1.5f), vec2(size.x, 3f))
        }
    }

    init {
        nodes.connectAdded(children::add)
        nodes.connectRemoved(children::remove)

        for (y in 1 .. 9) {
            for (x in 1 .. 9) {
                children.add(SudokuNodeDisplay(grid.item(y, x)))
            }
        }

        layout(GridLayout(9, 9)) {
            spacing(vec2(3f, 3f))
        }
    }
}
