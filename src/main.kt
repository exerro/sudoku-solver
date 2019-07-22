import ktaf.core.application
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.ui.elements.UIScrollContainer
import ktaf.ui.layout.*
import ktaf.ui.node.UIContainer
import ktaf.ui.scene.attachCallbacks
import ktaf.ui.scene.scene

fun main() = application("Sudoku solver") {
    display.width = 1280

    val context = DrawContext2D(screen)
    val scene = scene(display, context) {
        val container = root(UIContainer()) {
            layout(HDivLayout(80.pc() - 180.px()))

            val sudokuArea = children.add(UIContainer()) {
                colour(rgba(0.8f))
                padding(Border(32f))
                layout(FillLayout()) { alignment(vec2(0.5f)) }
            }

            val gridDisplay = sudokuArea.children.add(SudokuGridDisplay()) {
                computedHeight.connect(width::setter)
                colour(rgba(0.95f))
            }

            val listArea = children.add(UIScrollContainer()) {
                colour(rgba(0.2f))

                scrollbarY.width(16f)
                scrollbarY.padding(Border(3f))

                content.padding(Border(16f))
                content.layout(ListLayout()) {
                    spacing(Spacing.fixed(6f))
                }

                content.children.add(SudokuGridAction(
                        "Did some stuff super duper long stuff that will word wrap maybe?",
                        gridDisplay,
                        listOf(Pair(1, 1)),
                        listOf(Pair(2, 2))
                ))

                for (i in 1 .. 9) {
                    content.children.add(SudokuGridAction(
                            "Did some other stuff $i",
                            gridDisplay,
                            listOf(Pair(3, i)),
                            listOf(Pair(9, 6), Pair(5, 7))
                    ))
                }

                for (i in 1 .. 9) {
                    content.children.add(SudokuGridAction(
                            "Did some other other stuff $i",
                            gridDisplay,
                            listOf(Pair(i, 2)),
                            listOf(Pair(9, 6), Pair(5, 7))
                    ))
                }
            }
        }
    }

    scene.attachCallbacks(this)
}
