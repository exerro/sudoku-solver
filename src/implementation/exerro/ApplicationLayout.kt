package implementation.exerro

import framework.Rectangle

class ApplicationLayout(
        var headerArea: Rectangle,
        var headerContentArea: Rectangle,
        var sideArea: Rectangle,
        var contentArea: Rectangle,
        var gridArea: Rectangle,
        var wideContentArea: Rectangle,
        var wideGridArea: Rectangle
) {
    companion object {
        val EMPTY: ApplicationLayout = Rectangle(0f, 0f, 0f).let { e ->
            ApplicationLayout(e, e, e, e, e, e, e)
        }
    }
}
