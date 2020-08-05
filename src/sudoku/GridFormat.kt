package sudoku

/** Serialization format of a grid. */
enum class GridFormat {
    /** Items on a row are spaced apart by one character, with a '0'
     *  representing an empty slot. Rows are separated by '\n'. */
    SPACED_WITH_ZEROES,
    /** Items on a row are consecutively placed, with a '0' representing an
     *  empty slot. Rows are separated by '\n'. */
    ZEROES,
    /** Items on a row are consecutively placed, with a '.' representing an
     *  empty slot. Rows are separated by '\n'. */
    DOTS,
}
