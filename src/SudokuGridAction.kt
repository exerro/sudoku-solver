import SudokuNodeDisplay.Companion.HIGHLIGHT_GREEN
import SudokuNodeDisplay.Companion.HIGHLIGHT_RED
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.Font
import ktaf.ui.elements.UIButton
import ktaf.ui.elements.UILabel
import ktaf.ui.node.UIContainer
import ktaf.ui.node.push
import ktaf.ui.node.remove

class SudokuGridAction(
        action: String,
        grid: SudokuGridDisplay,
        red: List<Pair<Int, Int>>,
        green: List<Pair<Int, Int>>
): UIContainer() {
    val label = children.add(UIButton(action)) {
        alignment(vec2(0.5f))
    }

    init {
        label.font(Font.DEFAULT_FONT.scaleTo(24f))
//        label.colour(rgba())
        label.textColour(rgba(1f))

        label.onMouseEnter {
            red.forEach { (row, col) ->
                grid.nodes[(row - 1) * 9 + col - 1].red() }

            green.forEach { (row, col) ->
                grid.nodes[(row - 1) * 9 + col - 1].green() }
        }

        label.onMouseExit {
            red.forEach { (row, col) ->
                grid.nodes[(row - 1) * 9 + col - 1].none() }

            green.forEach { (row, col) ->
                grid.nodes[(row - 1) * 9 + col - 1].none() }
        }
    }
}
