package framework

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL46C
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths

private typealias FloatPair = Pair<Float, Float>

class Font(fontData: FontData) {
    val textureID: Int
    /** Top-left UV map. */
    val originUVs: Map<Char, FloatPair>
    /** Bottom-right UV map. */
    val deltaUVs: Map<Char, FloatPair>
    /** Normalised quad offset map. */
    val quadDelta: Map<Char, FloatPair>
    /** Normalised quad size map. */
    val quadSize: Map<Char, FloatPair>
    /** Normalised x-advance map. */
    val advances: Map<Char, Float>
    /** Original line height. */
    val lineHeight = fontData.lineHeight
    /** Normalised baseline. */
    val baseline = fontData.base / lineHeight

    init {
        val content = Files.readAllBytes(Paths.get(fontData.texturePath))
        val buffer = BufferUtils.createByteBuffer(content.size)
        buffer.put(content)
        buffer.flip()
        val w = BufferUtils.createIntBuffer(1)
        val h = BufferUtils.createIntBuffer(1)
        val channels = BufferUtils.createIntBuffer(1)
        val data = STBImage.stbi_load_from_memory(buffer, w, h, channels, STBImage.STBI_rgb_alpha)
                ?: throw RuntimeException(STBImage.stbi_failure_reason())
        val width = w.get()
        val height = h.get()

        checkError()
        textureID = GL46C.glGenTextures()
        checkError()
        GL46C.glBindTexture(GL46C.GL_TEXTURE_2D, textureID)
        checkError()
        GL46C.glTexParameteri(GL46C.GL_TEXTURE_2D, GL46C.GL_TEXTURE_MIN_FILTER, GL46C.GL_LINEAR)
        checkError()
        GL46C.glTexParameteri(GL46C.GL_TEXTURE_2D, GL46C.GL_TEXTURE_MAG_FILTER, GL46C.GL_LINEAR)
        checkError()
        GL46C.glTexImage2D(GL46C.GL_TEXTURE_2D, 0, GL46C.GL_RGBA, width, height, 0, GL46C.GL_RGBA, GL46C.GL_UNSIGNED_BYTE, data)
        checkError()
        GL46C.glBindTexture(GL46C.GL_TEXTURE_2D, 0)
        // TODO: wrapping?

        checkError()

        STBImage.stbi_image_free(data)

        val _originUVs = mutableMapOf<Char, FloatPair>()
        val _deltaUVs = mutableMapOf<Char, FloatPair>()
        val _quadDelta = mutableMapOf<Char, FloatPair>()
        val _quadSize = mutableMapOf<Char, FloatPair>()
        val _advances = mutableMapOf<Char, Float>()
        val sw = fontData.scaleW.toFloat()
        val sh = fontData.scaleH.toFloat()
        val fs = fontData.lineHeight.toFloat()

        fontData.chars.forEach { (ch, data) ->
            val char = ch.toChar()

            _originUVs[char] = data.x / sw to data.y / sh
            _deltaUVs[char] = data.width / sw to data.height / sh
            _quadDelta[char] = data.xOffset / fs to data.yOffset / fs
            _quadSize[char] = data.width / fs to data.height / fs
            _advances[char] = data.xAdvance / fs
        }

        originUVs = _originUVs
        deltaUVs = _deltaUVs
        quadDelta = _quadDelta
        quadSize = _quadSize
        advances = _advances
    }

    fun widthOf(text: String, scale: Float): Float {
        if (text.isEmpty()) return 0f
        val advances = text.dropLast(1).map { advances[it] ?: 0f }.sum()
        val last = (quadDelta[text.last()]?.first ?: 0f) + (quadSize[text.last()]?.first ?: 0f)
        return (advances + last) * scale
    }
}

private fun checkError() {
    val err = GL46C.glGetError()
    if (err != GL46C.GL_NO_ERROR) {
        error("GL error: $err")
    }
}
