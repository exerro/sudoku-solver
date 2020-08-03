import com.exerro.glfw.Window
import com.exerro.glfw.WindowProperty
import com.exerro.glfw.get
import org.lwjgl.opengl.GL46C.*

class QueuedGraphicsContext(
        val window: Window
): GraphicsContext {
    override fun clear(colour: Colour) {
        synchronized(queue) { queue.add(Command.Clear(colour)) }
    }

    override fun rectangle(rectangle: Rectangle, colour: Colour) {
        synchronized(queue) { queue.add(Command.Rect(rectangle, colour)) }
    }

    override fun line(start: Position, finish: Position, colour: Colour) {
        synchronized(queue) { queue.add(Command.Line(start, finish, colour)) }
    }

    override fun write(text: String, rectangle: Rectangle, colour: Colour, alignment: Alignment) {
        synchronized(queue) { queue.add(Command.Write(text, rectangle, colour, alignment)) }
    }

    fun renderQueue(): Boolean {
        if (queue.isEmpty()) return false

        val fbSize = window[WindowProperty.FRAMEBUFFER_SIZE]

        glViewport(0, 0, fbSize.width, fbSize.height)

        queue.forEach { when (it) {
            is Command.Clear -> {
                glClearColor(it.colour.red, it.colour.green, it.colour.blue, it.colour.alpha)
                glClear(GL_COLOR_BUFFER_BIT)
            }
            is Command.Rect -> {
                println("${it.rectangle} - $fbSize")
                glUseProgram(rectShader)
                glUniform4f(rectUniformColour, it.colour.red, it.colour.green, it.colour.blue, it.colour.alpha)
                glUniform2f(rectUniformPosition, it.rectangle.position.x, it.rectangle.position.y)
                glUniform2f(rectUniformSize, it.rectangle.size.width, it.rectangle.size.height)
                glUniform2f(rectUniformViewportSize, fbSize.width.toFloat(), fbSize.height.toFloat())
                glBindVertexArray(rectVAO)
                glDrawArrays(GL_TRIANGLES, 0, 6)
            }
            is Command.Line -> TODO()
            is Command.Write -> TODO()
        } }

        queue.clear()

        return true
    }

    private val queue: MutableList<Command> = mutableListOf()
    private val rectVAO = glGenVertexArrays()
    private val lineVAO = glGenVertexArrays()
    private val rectShader = loadShaders(RECT_FRAGMENT_SHADER, RECT_VERTEX_SHADER)
    private val rectUniformColour = glGetUniformLocation(rectShader, "u_colour")
    private val rectUniformPosition = glGetUniformLocation(rectShader, "u_position")
    private val rectUniformSize = glGetUniformLocation(rectShader, "u_size")
    private val rectUniformViewportSize = glGetUniformLocation(rectShader, "u_viewport")

    private sealed class Command {
        data class Clear(val colour: Colour): Command()
        data class Rect(val rectangle: Rectangle, val colour: Colour): Command()
        data class Line(val start: Position, val finish: Position, val colour: Colour): Command()
        data class Write(val text: String, val rectangle: Rectangle, val colour: Colour, val alignment: Alignment): Command()
    }

    init {
        val rectVBO = glGenBuffers()
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
