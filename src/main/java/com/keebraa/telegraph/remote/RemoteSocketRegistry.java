package com.keebraa.telegraph.remote;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.keebraa.telegraph.lib.MicroserviceDescriptor;

/**
 * Holds opened sockets and manipulates with the remote sockets (creating,
 * closing etc).
 * 
 * @author vvasianovych
 *
 */
public class RemoteSocketRegistry {

    private Map<String, Socket> sockets = new ConcurrentHashMap<>();

    public synchronized Socket resolveSocket(MicroserviceDescriptor descriptor) throws UnknownHostException, IOException {
        Socket socket = sockets.get(descriptor.getName());
        if (socket != null && socket.isConnected()) {
            return socket;
        }

        String host = descriptor.getHost();
        int port = descriptor.getPort();
        socket = new Socket(host, port);
        socket.setKeepAlive(true);
        sockets.put(descriptor.getName(), socket);
        return socket;
    }
}
