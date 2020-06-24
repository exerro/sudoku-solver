import observables.Observable

typealias SudokuGridItem = Observable<Int>
typealias SudokuGrid = List<List<SudokuGridItem>>

fun createEmptySudokuGrid(): SudokuGrid
        = (1 .. 9).map { (1 .. 9).map { Observable(0) } }
