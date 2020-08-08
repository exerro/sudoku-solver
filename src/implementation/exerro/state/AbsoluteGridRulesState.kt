package implementation.exerro.state

import framework.Colour
import framework.GraphicsContext
import implementation.exerro.ApplicationLayout
import implementation.exerro.ApplicationState
import sudoku.drawGrid
import sudoku.drawGridlines

class AbsoluteGridRulesState(
        override val layout: ApplicationLayout
) : ApplicationState {
    override val title = "Absolute Grid Rules"

    override fun draw(graphics: GraphicsContext) {
        graphics.drawGridlines(layout.gridArea)
        graphics.drawGrid(layout.gridArea) { area, location ->

        }
    }
}
