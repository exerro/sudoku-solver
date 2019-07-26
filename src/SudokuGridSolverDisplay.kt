import ktaf.core.rgba
import ktaf.graphics.Font
import ktaf.ui.elements.UIButton

class SudokuNodeSolverDisplay(node: Int) : UIButton("") {
    fun highlight(highlight: SudokuNodeSolverDisplayHighlight) {
        when (highlight) {
            SudokuNodeSolverDisplayHighlight.RED -> colour(rgba(0.9f, 0.3f, 0.3f, 0.2f))
            SudokuNodeSolverDisplayHighlight.GREEN -> colour(rgba(0.3f, 0.9f, 0.6f, 0.2f))
            SudokuNodeSolverDisplayHighlight.NONE -> colour(rgba(0.3f, 0.6f, 0.9f, 0f))
        }
    }

    init {
        when (node) {
            0 -> text("")
            else -> text(node.toString())
        }

        colour(rgba(0.3f, 0.6f, 0.9f, 0f))
        textColour(rgba(0.1f))
        font(Font.DEFAULT_FONT.scaleTo(28f))
    }
}

enum class SudokuNodeSolverDisplayHighlight {
    RED,
    GREEN,
    NONE
}

class SudokuGridSolverDisplay(grid: SudokuGrid): SudokuGridDisplayBase<SudokuNodeSolverDisplay>() {
    init {
        for (y in 1 .. 9) {
            for (x in 1 .. 9) {
                nodes.add(SudokuNodeSolverDisplay(grid[y - 1][x - 1].get()))
            }
        }
    }
}
