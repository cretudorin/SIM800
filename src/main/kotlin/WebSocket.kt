package sim868.kotlin

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI


class WebSocket(uri: URI) : WebSocketClient(uri) {
    init {
        super.connect()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {

    }

    override fun onMessage(message: String?) {
    }


    override fun onError(ex: Exception?) {
    }
}
