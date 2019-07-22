import ktaf.core.application
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
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

                children.add(SudokuGridDisplay()) {
                    computedHeight.connect(width::setter)
                    colour(rgba(0.95f))
                }
            }

            val listArea = children.add(UIContainer()) {
                colour(rgba(0.7f))
            }
        }
    }

    scene.attachCallbacks(this)
}
