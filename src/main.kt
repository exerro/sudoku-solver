import geometry.vec2
import ktaf.core.application
import ktaf.data.percent
import ktaf.data.px
import ktaf.graphics.rgba
import ktaf.gui.core.Padding
import ktaf.gui.core.UINode
import ktaf.gui.core.scene
import ktaf.gui.elements.hdiv
import ktaf.gui.elements.panel
import ktaf.gui.elements.stack
import ktaf.gui.elements.vdiv

fun main() = application {
    window("Sudoku solver", 1280, 720) { window ->
        val grid = createEmptySudokuGrid()

        val scene = scene<UINode>(window) {
            root = stack {
                panel(rgba(0.88f))

                hdiv {
                    val editView = vdiv(100.percent - 96.px) {
                        val sudokuArea = stack {
                            padding.value = Padding(32f)
                            alignment.value = vec2(0.5f)
                        }

                        sudokuArea.addChild(SudokuGridDisplay(grid)) {

                        }
                    }

                    val solverView = vdiv {

                    }
                }
            }

                    sudokuArea.children.add(SudokuGridDisplay(grid)) {
                        computedHeight.connect(width::setter)
                        colour(rgba(0.95f))
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

        scene.attach()
    }
}
