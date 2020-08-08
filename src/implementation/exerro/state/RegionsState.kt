package implementation.exerro.state

import framework.Alignment
import framework.Colour
import framework.GraphicsContext
import framework.resizeVerticalBy
import implementation.exerro.ApplicationLayout
import implementation.exerro.ApplicationState
import sudoku.drawGrid
import sudoku.drawGridlines

class RegionsState(
        override val layout: ApplicationLayout
) : ApplicationState {
    override val title = "Regions"

    override fun draw(graphics: GraphicsContext) {
        graphics.drawGridlines(layout.gridArea)
        graphics.drawGrid(layout.gridArea) { area, location ->

        }
    }
}
