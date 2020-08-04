package sudoku

/** Abstraction of a location in a grid. Use [GridLocation.from*] or
 *  [GridLocation.checkedFrom*] to create a location. Numeric indexes are always
 *  left-to-right and/or top-to-bottom, in that order, depending on what the
 *  index represents. */
class GridLocation private constructor(private val index: Int) {
    /** 0-based row of the location. */
    val row: Int get() = index / Grid.WIDTH
    /** 0-based column of the location. */
    val col: Int get() = index % Grid.WIDTH
    /** 0-based box of the location. Boxes start at the top left and work right
     *  first then downwards, i.e.
     *
     *  0 1 2
     *
     *  3 4 5
     *
     *  6 7 8 */
    val box: Int get() = TODO()
    /** 0-based absolute index (0-80) of the location, top left then right and
     *  downwards as with [box]. */
    val abs: Int get() = index

    /** 0-based index of the location within its row. */
    val indexInRow: Int get() = index % Grid.WIDTH
    /** 0-based index of the location within its column. */
    val indexInCol: Int get() = index / Grid.WIDTH
    /** 0-based index of the location within its box. */
    val indexInBox: Int get() = TODO()

    /** Return a new location relative to this one, moved [right] and [up] by
     *  some amount. If the resultant location lies outside the grid, return
     *  null instead. */
    fun relativeLocation(right: Int, up: Int): GridLocation? = when {
        row - up < 0 -> null
        row - up >= Grid.HEIGHT -> null
        col + right < 0 -> null
        col + right >= Grid.WIDTH -> null
        else -> GridLocation(index - up * Grid.WIDTH + right)
    }

    /** Return the location to the left of this, or null if that location lies
     *  outside the grid. */
    fun left() = relativeLocation(-1, 0)

    /** Return the location to the right of this, or null if that location lies
     *  outside the grid. */
    fun right() = relativeLocation(1, 0)

    /** Return the location above this, or null if that location lies outside
     *  the grid. */
    fun up() = relativeLocation(0, 1)

    /** Return the location below this, or null if that location lies outside
     *  the grid. */
    fun down() = relativeLocation(0, -1)

    companion object {
        /** Create a [GridLocation] from a [column] and a [row]. */
        fun fromRowAndColumn(row: Int, column: Int) = GridLocation(row * Grid.WIDTH + column)

        /** Create a [GridLocation] from an [index] into the given [row]. */
        fun fromIndexInRow(row: Int, index: Int) = GridLocation(row * Grid.WIDTH + index)

        /** Create a [GridLocation] from an [index] into the given [col]. */
        fun fromIndexInCol(col: Int, index: Int) = GridLocation(index * Grid.WIDTH + col)

        /** Create a [GridLocation] from an [index] into the given [box]. */
        fun fromIndexInBox(box: Int, index: Int) = GridLocation(TODO())

        /** Create a [GridLocation] from an absolute [index]. */
        fun fromAbsolute(index: Int) = GridLocation(index)

        /** Create a [GridLocation] from a [column] and a [row]. */
        fun checkedFromRowAndColumn(row: Int, column: Int) = fromRowAndColumn(row, column)
                .takeIf { row >= 0 && row < Grid.HEIGHT && column >= 0 && column < Grid.WIDTH }

        /** Create a [GridLocation] from an [index] into the given [row]. Return
         *  null if the [row] or [index] provided lies outside the grid. */
        fun checkedFromIndexInRow(row: Int, index: Int) = fromIndexInRow(row, index)
                .takeIf { row >= 0 && row < Grid.HEIGHT && index >= 0 && index < Grid.WIDTH }

        /** Create a [GridLocation] from an [index] into the given [col]. Return
         *  null if the [col] or [index] provided lies outside the grid. */
        fun checkedFromIndexInCol(col: Int, index: Int) = GridLocation(index * Grid.WIDTH + col)
                .takeIf { col >= 0 && col < Grid.WIDTH && index >= 0 && index < Grid.HEIGHT }

        /** Create a [GridLocation] from an [index] into the given [box]. Return
         *  null if the [box] or [index] provided lies outside the grid. */
        fun checkedFromIndexInBox(box: Int, index: Int) = GridLocation(TODO())
                .takeIf { box >= 0 && box < Grid.BOXES && index >= 0 && index < Grid.BOX_SIZE }

        /** Create a [GridLocation] from an absolute [index]. Return null if the
         *  [index] provided lies outside the grid. */
        fun checkedFromAbsolute(index: Int) = GridLocation(index)
                .takeIf { index >= 0 && index < Grid.WIDTH * Grid.HEIGHT }
    }
}
