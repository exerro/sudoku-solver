package sudoku

/** A grid of [WIDTH] * [HEIGHT] generic items. */
class Grid<Item> private constructor(private val items: List<Item>) {
    /** Map the values of the grid to new values using the current value and
     *  location. */
    fun <T> map(fn: (Item, GridLocation) -> T): Grid<T> = items
            .mapIndexed { index, item -> fn(item, GridLocation.fromAbsolute(index)) }
            .let { Grid(it) }

    /** Map the values of the grid to new values. */
    fun <T> mapValues(fn: (Item) -> T): Grid<T> = map { item, _ -> fn(item) }

    /** Map the values of the grid to new values using just the location. */
    fun <T> mapLocations(fn: (GridLocation) -> T): Grid<T> = map { _, location -> fn(location) }

    /** Get the item at the given [location] in the grid. */
    operator fun get(location: GridLocation): Item {
        val index = location.abs
        if (index < 0 || index >= WIDTH * HEIGHT) error("Invalid grid location (out of bounds)")
        return items[index]
    }

    /** Get a copy of the grid with the item at [location] set to [item]. */
    fun copy(location: GridLocation, item: Item): Grid<Item> =
            Grid(items.take(location.abs) + item + items.drop(location.abs + 2))

    companion object {
        /** Width of a grid. */
        const val WIDTH = 9
        /** Height of a grid. */
        const val HEIGHT = 9
        /** Number of boxes in a grid. */
        const val BOXES = 9
        /** Number of cells per box in a grid. */
        const val BOX_SIZE = 9

        /** An empty grid. */
        val EMPTY = Grid(Array(WIDTH * HEIGHT) { Unit }.toList())

        /** Load a grid from a serialized format. */
        fun load(gridContent: String, format: GridFormat): Grid<Int?> {
            val lines = gridContent.trim().split("\n").map { it.trim() }

            return EMPTY.mapLocations { location ->
                val line = lines.getOrNull(location.row) ?: ""
                val lineIndex = when (format) {
                    GridFormat.SPACED_WITH_ZEROES -> location.col * 2
                    GridFormat.ZEROES, GridFormat.DOTS -> location.col
                }

                when (val lineChar = line.getOrNull(lineIndex)) {
                    null -> null
                    in '1' .. '9' -> (lineChar - '0')
                    else -> null
                }
            }
        }

        /** Load a grid from a serialized format, approximating the format. */
        fun load(gridContent: String): Grid<Int?> {
            val format = when {
                gridContent.contains(' ') -> GridFormat.SPACED_WITH_ZEROES
                gridContent.contains('0') -> GridFormat.ZEROES
                gridContent.contains('.') -> GridFormat.DOTS
                else -> GridFormat.ZEROES
            }
            return load(gridContent, format)
        }
    }
}
