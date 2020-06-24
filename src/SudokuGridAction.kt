import geometry.vec2
import ktaf.graphics.Font
import ktaf.gui.elements.Button

class SudokuGridActionDisplay(
        private val display: SudokuGridSolverDisplay,
        action: String,
        red: List<Pair<Int, Int>>,
        green: List<Pair<Int, Int>>
): Button(action) {
    init {
        alignment(vec2(0.5f))
        font(Font.DEFAULT_FONT.scaleTo(24f))
        textColour(rgba(1f))
//        colour(rgba(1f))

        onMouseEnter {
            red.forEach { (row, col) ->
                display.nodes[(row - 1) * 9 + col - 1].highlight(SudokuNodeSolverDisplayHighlight.RED) }

            green.forEach { (row, col) ->
                display.nodes[(row - 1) * 9 + col - 1].highlight(SudokuNodeSolverDisplayHighlight.GREEN) }
        }

        onMouseExit {
            red.forEach { (row, col) ->
                display.nodes[(row - 1) * 9 + col - 1].highlight(SudokuNodeSolverDisplayHighlight.NONE) }

            green.forEach { (row, col) ->
                display.nodes[(row - 1) * 9 + col - 1].highlight(SudokuNodeSolverDisplayHighlight.NONE) }
        }
    }
}
