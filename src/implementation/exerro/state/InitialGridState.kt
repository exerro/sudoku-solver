package implementation.exerro.state

import framework.*
import implementation.exerro.ApplicationLayout
import implementation.exerro.ApplicationState
import sudoku.Grid
import sudoku.drawGrid
import sudoku.drawGridlines

class InitialGridState(
        override val layout: ApplicationLayout
): ApplicationState {
    override val title = "Initial Grid"
    override val enableSidebar: Boolean get() = false

    var grid = Grid.EMPTY.mapValues { null as Int? }

    override fun draw(graphics: GraphicsContext) {
        graphics.drawGridlines(layout.wideGridArea)
        graphics.drawGrid(layout.wideGridArea) { area, location ->
            val item = grid[location]
            if (item != null) graphics.write(
                    item.toString(),
                    area.resizeBy(0.8f).translateVerticalRelative(0.05f),
                    Colour.grey
            )
        }
    }
}
