package com.guet.flexbox.handshake

import com.guet.flexbox.handshake.event.NetworkChangedEvent
import com.guet.flexbox.handshake.event.PortHasSetEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

/***
 * 要在Java中跨平台的获取真正的首选出站ip，只能用这种极其恶心的写法
 */
@Component
class NetworkWatcher : Thread() {

    private val logger = LoggerFactory.getLogger(NetworkWatcher::class.java)

    @Autowired
    private lateinit var context: ApplicationContext

    @Autowired
    private lateinit var attributes: ConcurrentHashMap<String, Any>

    private val watcherService = ServerSocket(10086)

    override fun run() {
        while (true) {
            val inSocket = Socket()
            inSocket.connect(InetSocketAddress(InetAddress.getLocalHost(), 10086))
            val outSocket = watcherService.accept()
            val outAddress = outSocket.localAddress.hostAddress
            inSocket.close()
            if (!outAddress.isNullOrEmpty() && outAddress != attributes["host"]) {
                attributes["host"] = outAddress
                val port = attributes["port"] as? Int ?: 8080
                val url = "http://$outAddress:${port}"
                logger.info("Network changed: $url")
                context.publishEvent(
                        NetworkChangedEvent(this, outAddress)
                )
            }
            sleep(1000)
        }
    }

    @EventListener
    fun onInitialized(event: PortHasSetEvent) {
        start()
    }
}