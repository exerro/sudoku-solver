import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.graphics.Font
import ktaf.graphics.rectangle
import ktaf.typeclass.plus
import ktaf.ui.elements.UIButton
import ktaf.ui.layout.GridLayout
import ktaf.ui.node.*
import org.lwjgl.glfw.GLFW

class SudokuNodeDisplay(node: SudokuGridItem) : UIButton("") {

    init {
        node.connect { when (it) {
            0 -> text("")
            else -> text(it.toString())
        } }

        colour(rgba(0.3f, 0.6f, 0.9f, 0f))
        colour[ACTIVE](rgba(0.3f, 0.6f, 0.9f, 0.2f))
        textColour(rgba(0.1f))
        font(Font.DEFAULT_FONT.scaleTo(28f))

        focused.connect { when (it) {
            true -> state.push(ACTIVE)
            false -> state.remove(ACTIVE)
        } }

        addHotkey(GLFW.GLFW_KEY_DOWN) {
            if (!focused.get()) return@addHotkey
            generateSequence(this, UINode::nextChild).toList().getOrNull(9) ?.focused ?.set(true)
        }

        addHotkey(GLFW.GLFW_KEY_UP) {
            if (!focused.get()) return@addHotkey
            generateSequence(this, UINode::previousChild).toList().getOrNull(9) ?.focused ?.set(true)
        }

        addHotkey(GLFW.GLFW_KEY_LEFT) {
            if (!focused.get()) return@addHotkey
            previousChild() ?.focused ?.set(true)
        }

        addHotkey(GLFW.GLFW_KEY_RIGHT) {
            if (!focused.get()) return@addHotkey
            nextChild() ?.focused ?.set(true)
        }

        addHotkey(GLFW.GLFW_KEY_SPACE) {
            if (!focused.get()) return@addHotkey
            node(0)
            nextChild() ?.focused ?.set(true) ?: focused(false)
        }

        (1 .. 9).forEach { n ->
            addHotkey(GLFW.GLFW_KEY_0 + n) {
                if (!focused.get()) return@addHotkey
                node(n)
                nextChild() ?.focused ?.set(true) ?: focused(false)
            }
        }
    }

    companion object {
        const val ACTIVE = "active"
    }
}

open class SudokuGridDisplayBase<SudokuNodeDisplay: UINode> : UIContainer() {
    val nodes = KTAFList<SudokuNodeDisplay>()

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

        layout(GridLayout(9, 9)) {
            spacing(vec2(3f, 3f))
        }
    }
}

class SudokuGridDisplay(private val grid: SudokuGrid = createEmptySudokuGrid()): SudokuGridDisplayBase<SudokuNodeDisplay>() {
    init {
        for (y in 1 .. 9) {
            for (x in 1 .. 9) {
                nodes.add(SudokuNodeDisplay(grid[y - 1][x - 1]))
            }
        }
    }

    fun solver() = SudokuGridSolverDisplay(grid)
}
