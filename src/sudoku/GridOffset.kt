package sudoku

/** An offset to a location in a grid. */
data class GridOffset(val right: Int, val up: Int) {
    companion object {
        val NONE = GridOffset(0, 0)
    }
}

operator fun GridLocation.plus(offset: GridOffset) = relativeLocation(offset.right, offset.up)
