package implementation.exerro.state

import framework.GraphicsContext
import implementation.exerro.ApplicationLayout
import implementation.exerro.ApplicationState
import sudoku.drawGridOffsetLines
import sudoku.drawGridOffsets

class RelativeGridRulesState(
        override val layout: ApplicationLayout
) : ApplicationState {
    override val title = "Relative Grid Rules"

    override fun draw(graphics: GraphicsContext) {
        graphics.drawGridOffsetLines(layout.gridArea)
        graphics.drawGridOffsets(layout.gridArea) { offset, area ->

        }
    }
}
