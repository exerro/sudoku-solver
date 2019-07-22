import ktaf.core.KTAFValue

class SudokuGrid {
    val items = (1..9).map { (1..9).map { KTAFValue(0) } }

    fun item(row: Int, col: Int) = items[row - 1][col - 1]

    init {
        items.forEach { it.forEach { item ->
            item((1 + Math.random() * 9).toInt() * (Math.random() * 2).toInt())
        } }
    }
}
