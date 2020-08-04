package framework

import com.exerro.glfw.Window
import com.exerro.glfw.WindowProperty
import com.exerro.glfw.data.WindowSize
import com.exerro.glfw.get
import org.lwjgl.opengl.GL46C.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.exp
import kotlin.math.max

// TODO!!!
//  mad flicker caused by the change system and double buffering
//  draw XYZ, flip buffer, then draw W and flip buffer again and only W is
//  visible

class QueuedGraphicsContext(
        private val window: Window
): GraphicsContext {
    override fun begin() {
        lock.lock()
    }

    override fun clear(colour: Colour) {
        synchronized(queue) {
            queue.clear()
            queue.add(Command.Clear(colour))
            nextDirtyIndex = 0
        }
    }

    override fun rectangle(rectangle: Rectangle, colour: Colour) {
        synchronized(queue) { queue.add(Command.Rect(rectangle, colour)) }
    }

    override fun line(start: Position, finish: Position, colour: Colour, thickness: Float, colour2: Colour) {
        synchronized(queue) { queue.add(Command.Line(start, finish, colour)) }
    }

    override fun write(text: String, rectangle: Rectangle, colour: Colour, alignment: Alignment) {
        synchronized(queue) { queue.add(Command.Write(text, rectangle, colour, alignment)) }
    }

    override fun finish() {
        lock.unlock()
    }

    ////////////////////////////////////////////////////////////////////////////

    fun makeDirty() {
        nextDirtyIndex = 0
    }

    fun hasChanges(): Boolean = nextDirtyIndex > queue.size

    fun renderChanges() {
        lock.lock()

        synchronized(queue) {
            val fbSize = window[WindowProperty.FRAMEBUFFER_SIZE]
            glViewport(0, 0, fbSize.width, fbSize.height)
            queue.drop(nextDirtyIndex).forEach { renderCommand(it, fbSize) }
            nextDirtyIndex = queue.size
        }

        lock.unlock()
    }

    //////////////////////////////////////////////////////////////////////////////////////

    private fun renderCommand(command: Command, fbSize: WindowSize): Unit = when (command) {
        is Command.Clear -> {
            glClearColor(command.colour.red, command.colour.green, command.colour.blue, command.colour.alpha)
            glClear(GL_COLOR_BUFFER_BIT)
        }
        is Command.Rect -> {
            glUseProgram(rectShader)
            glUniform4f(rectUniformColour, command.colour.red, command.colour.green, command.colour.blue, command.colour.alpha)
            glUniform2f(rectUniformPosition, command.rectangle.position.x, command.rectangle.position.y)
            glUniform2f(rectUniformSize, command.rectangle.size.width, command.rectangle.size.height)
            glUniform2f(rectUniformViewportSize, fbSize.width.toFloat(), fbSize.height.toFloat())
            glBindVertexArray(rectVAO)
            glDrawArrays(GL_TRIANGLES, 0, 6)
        }
        is Command.Line -> TODO()
        is Command.Write -> {
            val scale = command.rectangle.size.height
            val width = font.widthOf(command.text, scale)
            var currentX = command.rectangle.position.x +
                    (command.rectangle.size.width - width) * command.alignment.numeric
            val baselineY = command.rectangle.position.y + font.baseline * scale

            println("$currentX, ${fbSize.width}, $width")

            glUseProgram(textShader)
            glBindTexture(GL_TEXTURE_2D, font.textureID)
            glUniform4f(textUniformColour, command.colour.red, command.colour.green, command.colour.blue, command.colour.alpha)
            glUniform2f(textUniformViewportSize, fbSize.width.toFloat(), fbSize.height.toFloat())
            glBindVertexArray(rectVAO)

            val min = max(0.04f, -0.34f / (1 + 234f * exp(-0.335f * scale)) + 17.079f * (1 - 1 / (7.84f + scale)) - 16.1f)
            val max = 2.9f / (7.78f + scale) + 0.567f

            glUniform2f(textUniformDUThreshold, min, min + (1 - min) * max)

            command.text.forEach {
                val (dx, dy) = font.quadDelta[it] ?: 0f to 0f
                val (qw, qh) = font.quadSize[it] ?: 0f to 0f
                val (ux, uy) = font.originUVs[it] ?: 0f to 0f
                val (udx, udy) = font.deltaUVs[it] ?: 0f to 0f

                glUniform2f(textUniformPosition, (currentX + dx * scale).toInt().toFloat(), baselineY + dy * scale)
                glUniform2f(textUniformSize, qw * scale, qh * scale)
                glUniform2f(textUniformUVOrigin, ux, uy)
                glUniform2f(textUniformUVSize, udx, udy)
                glDrawArrays(GL_TRIANGLES, 0, 6)

                currentX += (font.advances[it] ?: 0f) * scale
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private val lock = ReentrantLock()
    private val queue: MutableList<Command> = mutableListOf()
    private val rectVAO = glGenVertexArrays()
    private val lineVAO = glGenVertexArrays()

    private val rectShader = loadShaders(RECT_FRAGMENT_SHADER, RECT_VERTEX_SHADER)
    private val rectUniformColour = glGetUniformLocation(rectShader, "u_colour")
    private val rectUniformPosition = glGetUniformLocation(rectShader, "u_position")
    private val rectUniformSize = glGetUniformLocation(rectShader, "u_size")
    private val rectUniformViewportSize = glGetUniformLocation(rectShader, "u_viewport")

    private val textShader = loadShaders(TEXT_FRAGMENT_SHADER, TEXT_VERTEX_SHADER)
    private val textUniformColour = glGetUniformLocation(textShader, "u_colour")
    private val textUniformDUThreshold = glGetUniformLocation(textShader, "du_threshold")
    private val textUniformPosition = glGetUniformLocation(textShader, "u_position")
    private val textUniformSize = glGetUniformLocation(textShader, "u_size")
    private val textUniformViewportSize = glGetUniformLocation(textShader, "u_viewport")
    private val textUniformUVOrigin = glGetUniformLocation(textShader, "u_uv_origin")
    private val textUniformUVSize = glGetUniformLocation(textShader, "u_uv_size")

    private var nextDirtyIndex = 0
    private val font = Font(FontData.MONOID)

    ////////////////////////////////////////////////////////////////////////////

    private sealed class Command {
        data class Clear(val colour: Colour): Command()
        data class Rect(val rectangle: Rectangle, val colour: Colour): Command()
        data class Line(val start: Position, val finish: Position, val colour: Colour): Command()
        data class Write(val text: String, val rectangle: Rectangle, val colour: Colour, val alignment: Alignment): Command()
    }

    init {
        val rectVBO = glGenBuffers()
        val uvVBO = glGenBuffers()
        val lineVBO = glGenBuffers()

        glBindVertexArray(rectVAO)
        glBindBuffer(GL_ARRAY_BUFFER, rectVBO)
        glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
                0f, 0f, 0f,
                0f, 1f, 0f,
                1f, 1f, 0f,
                0f, 0f, 0f,
                1f, 1f, 0f,
                1f, 0f, 0f
        ), GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, rectVBO)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        glBindBuffer(GL_ARRAY_BUFFER, uvVBO)
        glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
                0f, 0f,
                0f, 1f,
                1f, 1f,
                0f, 0f,
                1f, 1f,
                1f, 0f
        ), GL_STATIC_DRAW)

        glEnableVertexAttribArray(1)
        glBindBuffer(GL_ARRAY_BUFFER, uvVBO)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0)

        glBindVertexArray(lineVAO)
        glBindBuffer(GL_ARRAY_BUFFER, lineVBO)
        glBufferData(GL_ARRAY_BUFFER, floatArrayOf(
                0f,
                1f
        ), GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, lineVBO)
        glVertexAttribPointer(0, 1, GL_FLOAT, false, 0, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private fun loadShaders(fragment: String, vertex: String): Int {
        val vertexID = glCreateShader(GL_VERTEX_SHADER)
        val fragmentID = glCreateShader(GL_FRAGMENT_SHADER)
        val programID = glCreateProgram()

        glShaderSource(vertexID, vertex)
        glCompileShader(vertexID)

        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            error("Error loading vertex shader: ${glGetShaderInfoLog(vertexID)}")
        }

        glShaderSource(fragmentID, fragment)
        glCompileShader(fragmentID)

        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            error("Error loading fragment shader: ${glGetShaderInfoLog(fragmentID)}")
        }

        glAttachShader(programID, vertexID)
        glAttachShader(programID, fragmentID)
        glLinkProgram(programID)

        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            error("Error linking shader: ${glGetProgramInfoLog(programID)}")
        }

        glDetachShader(programID, vertexID)
        glDetachShader(programID, fragmentID)
        glDeleteShader(vertexID)
        glDeleteShader(fragmentID)

        return programID
    }
}

private const val RECT_VERTEX_SHADER = """
#version 440 core

uniform vec2 u_position = vec2(0, 0);
uniform vec2 u_size = vec2(100, 100);
uniform vec2 u_viewport = vec2(1000, 1000);

layout (location = 0) in vec3 position;

void main(void) {
    vec2 cornerA = (2 * u_position / u_viewport - vec2(1, 1)) * vec2(1, -1);
    vec2 sizeDelta = 2 * u_size / u_viewport * vec2(1, -1);
    gl_Position.xyz = vec3(cornerA, 1) + vec3(sizeDelta, 1) * position;
    gl_Position.w = 1;
}
"""

private const val RECT_FRAGMENT_SHADER = """
#version 440 core

uniform vec4 u_colour;

out vec4 fragment_colour;

void main(void) {
    fragment_colour = u_colour;
}
"""

private const val TEXT_VERTEX_SHADER = """
#version 440 core

uniform vec2 u_position = vec2(0, 0);
uniform vec2 u_size = vec2(100, 100);
uniform vec2 u_viewport = vec2(1000, 1000);
uniform vec2 u_uv_origin;
uniform vec2 u_uv_size;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;

out vec2 fragment_uv;

void main(void) {
    vec2 cornerA = (2 * u_position / u_viewport - vec2(1, 1)) * vec2(1, -1);
    vec2 sizeDelta = 2 * u_size / u_viewport * vec2(1, -1);
    fragment_uv = u_uv_origin + uv * u_uv_size;
    gl_Position.xyz = vec3(cornerA, 1) + vec3(sizeDelta, 1) * position;
    gl_Position.w = 1;
}
"""

private const val TEXT_FRAGMENT_SHADER = """
#version 440 core

uniform sampler2D u_texture;
uniform vec4 u_colour;
uniform vec2 du_threshold;

in vec2 fragment_uv;

out vec4 fragment_colour;

void main(void) {
//    fragment_colour = u_colour;
    float alpha = texture(u_texture, fragment_uv).w;
    float t = clamp((alpha - du_threshold.x) / (max(du_threshold.x + 0.01, du_threshold.y) - du_threshold.x), 0, 1);
    
    fragment_colour = u_colour * vec4(1, 1, 1, t);
}
"""
