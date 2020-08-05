package sudoku

/** Stores the history of a grid as it undergoes a series of actions. */
class GridHistory<Item, Action> private constructor(
        /** First grid in the history. */
        val initialGrid: Grid<Item>,
        private val history: List<Pair<Action, Grid<Item>>>
) {
    /** Total number of actions that have been applied on top of the initial
     *  grid. */
    val totalActions = history.size
    /** Most recent grid, having undergone all the actions in the history. */
    val latestGrid: Grid<Item> = history.lastOrNull()?.second ?: initialGrid

    /** Get the n-th grid in the history, where [n]=0 is the initial grid, and
     *  [n]=[totalActions] is the most recent. */
    fun getGrid(n: Int) = when (n) { 0 -> initialGrid; else -> history[n - 1].second }
    /** Get the n-th action in the history, where [n]=0 is the first action
     *  applied on top of the initial grid, and [n]=[totalActions]-1 is the most
     *  recent. */
    fun getAction(n: Int) = history[n].first

    /** Commit an [action] and resultant [grid] on top of this history and
     *  return the new history object. */
    fun commit(action: Action, grid: Grid<Item>) =
            GridHistory(initialGrid, history + (action to grid))

    companion object {
        /** Create an empty history object from an initial grid with no actions
         *  applied to it. */
        fun <Item, Action> empty(initialGrid: Grid<Item>) =
                GridHistory<Item, Action>(initialGrid, listOf())
    }
}
