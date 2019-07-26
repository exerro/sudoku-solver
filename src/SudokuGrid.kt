import ktaf.core.KTAFValue

typealias SudokuGridItem = KTAFValue<Int>
typealias SudokuGrid = List<List<SudokuGridItem>>

fun createEmptySudokuGrid(): SudokuGrid
        = (1 .. 9).map { (1 .. 9).map { KTAFValue(0) } }
