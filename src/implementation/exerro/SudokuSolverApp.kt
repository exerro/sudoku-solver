package implementation.exerro

import com.exerro.glfw.GLFWInstance
import com.exerro.glfw.Window
import com.exerro.glfw.data.Key
import com.exerro.glfw.data.KeyModifier
import framework.*
import framework.internal.QueuedGraphicsContext
import implementation.exerro.state.AbsoluteGridRulesState
import implementation.exerro.state.InitialGridState
import implementation.exerro.state.RegionsState
import implementation.exerro.state.RelativeGridRulesState
import sudoku.*
import kotlin.math.abs

fun main() {
    Application.launch(::SudokuSolverApp)
}

class SudokuSolverApp(window: Window.Default, graphics: QueuedGraphicsContext, private val glfw: GLFWInstance) : Application(window, graphics, glfw) {
    val layout = ApplicationLayout.EMPTY
    val initialGridState = InitialGridState(layout)
    val regionsState = RegionsState(layout)
    val absoluteRulesState = AbsoluteGridRulesState(layout)
    val relativeRulesState = RelativeGridRulesState(layout)
    val states = listOf(initialGridState, regionsState, absoluteRulesState, relativeRulesState)
    var state: ApplicationState = initialGridState

    override fun windowResized() {
        val (h, nh) = Rectangle(Position.origin, windowSize).splitVertical(0.1f)
        val (s, c) = nh.splitHorizontal(0.3f)

        layout.headerArea = h
        layout.headerContentArea = h.withPadding(h.size.height * 0.2f)
        layout.wideContentArea = nh
        layout.wideGridArea = nh.minSquare().resizeBy(0.8f)
        layout.sideArea = s
        layout.contentArea = c
        layout.gridArea = c.minSquare().resizeBy(0.8f)
    }

    override fun keyPressed(key: Key, modifiers: Set<KeyModifier>) { when {
        key == Key.V && KeyModifier.CONTROL in modifiers -> {
            val clipboardText = glfw.getClipboard()
            clipboardText?.let(state::paste)
        }
        key == Key.Q -> {
            states.getOrNull(states.indexOf(state) - 1)?.let { state = it; draw() }
        }
        key == Key.E -> {
            states.getOrNull(states.indexOf(state) + 1)?.let { state = it; draw() }
        }
        else -> state.keyPressed(key, modifiers)
    } }

    override fun draw() {
        val currentStateIndex = states.indexOf(state)
        val previousState = states.getOrNull(currentStateIndex - 1)
        val nextState = states.getOrNull(currentStateIndex + 1)

        graphics.begin()
        graphics.clear(Colour.white)
        graphics.rectangle(layout.headerArea, Colour.lightGrey)
        if (state.enableSidebar) graphics.rectangle(layout.sideArea, Colour.ultraLightGrey)

        graphics.write(state.title, layout.headerContentArea, Colour.darkGrey)
        if (previousState != null) graphics.write("< ${previousState.title} (Q)", layout.headerContentArea.resizeVerticalBy(0.6f), Colour.blue, Alignment.Left)
        if (nextState != null) graphics.write("(E) ${nextState.title} >", layout.headerContentArea.resizeVerticalBy(0.6f), Colour.blue, Alignment.Right)
        state.draw(graphics)

        graphics.finish()
    }
}
