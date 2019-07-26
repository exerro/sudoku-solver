import ktaf.core.application
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.ui.elements.UIButton
import ktaf.ui.elements.UIScrollContainer
import ktaf.ui.elements.UIView
import ktaf.ui.layout.*
import ktaf.ui.node.UIContainer
import ktaf.ui.scene.attachCallbacks
import ktaf.ui.scene.scene

fun main() = application("Sudoku solver") {
    display.width = 1280

    val grid = createEmptySudokuGrid()

    val context = DrawContext2D(screen)
    val scene = scene(display, context) {
        val container = root(UIView()) {
            horizontal()
            colour(rgba(0.88f))
        }

        val editView = container.children.add(UIContainer()) {
            layout(VDivLayout(100.pc() - 96.px()))

            val sudokuArea = children.add(UIContainer()) {
                padding(Border(32f))
                layout(FillLayout()) { alignment(vec2(0.5f)) }
            }

            sudokuArea.children.add(SudokuGridDisplay(grid)) {
                computedHeight.connect(width::setter)
                colour(rgba(0.95f))
            }
        }

        val solverView = container.children.add(UIContainer()) {
            layout(HDivLayout(80.pc() - 180.px()))
        }

        val solverSudokuArea = solverView.children.add(UIContainer()) {
            padding(Border(32f))
            layout(FillLayout()) { alignment(vec2(0.5f)) }
        }

        solverView.children.add(UIContainer()) {
            layout(VDivLayout(100.pc() - 96.px()))
            colour(rgba(0.8f))

            children.add(UIScrollContainer()) {
                scrollbarY.width(16f)
                scrollbarY.padding(Border(3f))

                content.padding(Border(16f))
                content.layout(ListLayout()) {
                    spacing(Spacing.fixed(6f))
                }
            }

            children.add(UIButton("BACK")) {
                margin(Border(16f, 32f))
                onClick { container.show(editView) }
            }
        }

        editView.children.add(UIButton("SOLVE")) {
            width(256f)
            margin(Border(bottom = 32f))

            onClick {
                container.show(solverView)
                solverSudokuArea.children.clear()

                solverSudokuArea.children.add(SudokuGridSolverDisplay(grid)) {
                    computedHeight.connect(width::setter)
                    colour(rgba(0.95f))
                }
            }
        }
    }

    scene.attachCallbacks(this)
}
