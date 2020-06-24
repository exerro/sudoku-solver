import ktaf.graphics.rgba
import ktaf.gui.elements.Button
import ktaf.gui.elements.Stack

class SudokuNodeSolverDisplay(node: Int) : Stack() {
    val button = this.addChild(Button(if (node == 0) "" else node.toString()))

    fun highlight(highlight: SudokuNodeSolverDisplayHighlight) {
        button.colour.value = when (highlight) {
            SudokuNodeSolverDisplayHighlight.RED -> rgba(0.9f, 0.3f, 0.3f, 0.2f)
            SudokuNodeSolverDisplayHighlight.GREEN -> rgba(0.3f, 0.9f, 0.6f, 0.2f)
            SudokuNodeSolverDisplayHighlight.NONE -> rgba(0.3f, 0.6f, 0.9f, 0f)
        }
    }

    init {
        button.colour.value = rgba(0.3f, 0.6f, 0.9f, 0f)
        button.textColour.value = rgba(0.1f)
//        button.font.value = Font.DEFAULT_FONT.scaleTo(28f)
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
                nodes.add(SudokuNodeSolverDisplay(grid[y - 1][x - 1].value))
            }
        }
    }
}
