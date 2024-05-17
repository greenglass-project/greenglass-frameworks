import io.javalin.websocket.WsContext
import org.eclipse.jetty.websocket.api.*
import java.net.SocketAddress
import java.time.Duration


class TestSession : Session {
    override fun getBehavior(): WebSocketBehavior = TODO("Not yet implemented")
    override fun getIdleTimeout(): Duration = TODO("Not yet implemented")
    override fun getInputBufferSize(): Int = TODO("Not yet implemented")
    override fun getOutputBufferSize(): Int = TODO("Not yet implemented")
    override fun getMaxBinaryMessageSize(): Long = TODO("Not yet implemented")
    override fun getMaxTextMessageSize(): Long = TODO("Not yet implemented")
    override fun getMaxFrameSize(): Long = TODO("Not yet implemented")
    override fun isAutoFragment(): Boolean = TODO("Not yet implemented")
    override fun setIdleTimeout(duration: Duration?) = TODO("Not yet implemented")
    override fun setInputBufferSize(size: Int) = TODO("Not yet implemented")
    override fun setOutputBufferSize(size: Int) = TODO("Not yet implemented")
    override fun setMaxBinaryMessageSize(size: Long) = TODO("Not yet implemented")
    override fun setMaxTextMessageSize(size: Long) = TODO("Not yet implemented")
    override fun setMaxFrameSize(maxFrameSize: Long) = TODO("Not yet implemented")
    override fun setAutoFragment(autoFragment: Boolean) = TODO("Not yet implemented")
    override fun close() = TODO("Not yet implemented")
    override fun close(closeStatus: CloseStatus?) = TODO("Not yet implemented")
    override fun close(statusCode: Int, reason: String?) = TODO("Not yet implemented")
    override fun disconnect() = TODO("Not yet implemented")
    override fun getLocalAddress(): SocketAddress = TODO("Not yet implemented")
    override fun getProtocolVersion(): String = TODO("Not yet implemented")
    override fun getRemote(): RemoteEndpoint = TODO("Not yet implemented")
    override fun getRemoteAddress(): SocketAddress = TODO("Not yet implemented")
    override fun getUpgradeRequest(): UpgradeRequest = TODO("Not yet implemented")
    override fun getUpgradeResponse(): UpgradeResponse = TODO("Not yet implemented")
    override fun isOpen(): Boolean = TODO("Not yet implemented")
    override fun isSecure(): Boolean = TODO("Not yet implemented")
    override fun suspend(): SuspendToken = TODO("Not yet implemented")
}

class TestContext(sessionId : String, session : Session) : WsContext(sessionId, session)

class WebServiceTests {

    //@Test
    fun test1() {

        val session = TestSession()
        val ctx = TestContext("12345", session)

    }

}