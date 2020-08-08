package sudoku

typealias GridLocations = Set<GridLocation>

/** Abstraction of a location in a grid. Use [GridLocation.*] to create a
 *  location. */
class GridLocation private constructor(private val index: Int) {
    /** 0-based row of the location. */
    val row: Int get() = index / Grid.COLUMNS

    /** 0-based column of the location. */
    val column: Int get() = index % Grid.COLUMNS

    /** 0-based box of the location. A box is a 3x3 region. Boxes start at the
     *  top left and work right first then downwards, i.e.
     *
     *  0 1 2
     *
     *  3 4 5
     *
     *  6 7 8 */
    val box: Int get() {
        val boxY = index / Grid.COLUMNS / Grid.VBOXES
        val boxX = index % Grid.COLUMNS / Grid.HBOXES
        return boxY * Grid.HBOXES + boxX
    }

    /** 0-based index of the location within its box, starting at the top left
     *  and working right and downwards. */
    val cellInBox: Int get() {
        val boxY = index / Grid.COLUMNS % Grid.BOX_ROWS
        val boxX = index % Grid.COLUMNS % Grid.BOX_COLUMNS
        return boxY * Grid.BOX_COLUMNS + boxX
    }

    /** 0-based absolute index (0-80) of the location, top left then right and
     *  downwards as with [box]. */
    val absolute: Int get() = index

    /** Return a new location relative to this one, moved [right] and [up] by
     *  some amount. If the resultant location lies outside the grid, return
     *  null instead. */
    fun relativeLocation(right: Int, up: Int): GridLocation? = when {
        row - up < 0 -> null
        row - up >= Grid.ROWS -> null
        column + right < 0 -> null
        column + right >= Grid.COLUMNS -> null
        else -> GridLocation(index - up * Grid.COLUMNS + right)
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
        /** Create a [GridLocation] from an [column] into the given [row]. */
        fun fromRowAndColumn(row: Int, column: Int) = GridLocation(row * Grid.COLUMNS + column)

        /** Create a [GridLocation] from an [cell] into the given [box]. */
        fun fromCellInBox(box: Int, cell: Int): GridLocation {
            val tlIndex = box / Grid.HBOXES * Grid.BOX_ROWS * Grid.COLUMNS +
                          box % Grid.HBOXES * Grid.BOX_COLUMNS
            val boxOffset = cell / Grid.BOX_COLUMNS * Grid.COLUMNS +
                            cell % Grid.BOX_COLUMNS
            return GridLocation(tlIndex + boxOffset)
        }

        /** Create a [GridLocation] from an absolute [index]. */
        fun fromAbsolute(index: Int) = GridLocation(index)

        /** Create a [GridLocation] from a [column] into the given [row]. Return
         *  null if the [row] or [column] provided lies outside the grid. */
        fun checkedFromRowAndColumn(row: Int, column: Int) = fromRowAndColumn(row, column)
                .takeIf { row >= 0 && row < Grid.ROWS && column >= 0 && column < Grid.COLUMNS }

        /** Create a [GridLocation] from an [cell] into the given [box]. Return
         *  null if the [box] or [cell] provided lies outside the grid. */
        fun checkedFromCellInBox(box: Int, cell: Int) = fromCellInBox(box, cell)
                .takeIf { box >= 0 && box < Grid.BOXES && cell >= 0 && cell < Grid.BOX_SIZE }

        /** Create a [GridLocation] from an absolute [index]. Return null if the
         *  [index] provided lies outside the grid. */
        fun checkedFromAbsolute(index: Int) = GridLocation(index)
                .takeIf { index >= 0 && index < Grid.COLUMNS * Grid.ROWS }

        /** Return a set of all grid locations in [row]. */
        fun row(row: Int): GridLocations =
                (0 until Grid.COLUMNS).map { fromRowAndColumn(row, it) }.toSet()

        /** Return a set of all grid locations in [column]. */
        fun column(column: Int): GridLocations =
                (0 until Grid.ROWS).map { fromRowAndColumn(it, column) }.toSet()

        /** Return a set of all grid locations in [box]. */
        fun box(box: Int): GridLocations =
                (0 until Grid.BOX_SIZE).map { fromCellInBox(box, it) }.toSet()
    }

    //////////////////////////////////////////////////////////////////////////////////////

    override fun equals(other: Any?) = (other as? GridLocation)?.index == index
    override fun hashCode() = index
    override fun toString() = "GridLocation($row, $column)"
}
