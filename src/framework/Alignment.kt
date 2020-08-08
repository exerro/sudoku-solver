package framework

/** Horizontal alignment of text. */
enum class Alignment(
        /** Ratio of any free space to place to the left of the text. */
        val numeric: Float
) {
    Left(0f),
    Right(1f),
    Centre(0.5f)
}
