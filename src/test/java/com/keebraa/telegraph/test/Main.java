package com.keebraa.telegraph.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.UUID;

public class Main {

    protected static byte[] buf = new byte[256];

    private static UUID id = UUID.randomUUID();
    
    public static void main(String[] args) throws IOException {
        MulticastSocket socket = new MulticastSocket(4446);
        InetAddress group = InetAddress.getByName("230.0.0.0");
        socket.joinGroup(group);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println(id.toString() + ":: "+received);
            if ("end".equals(received)) {
                break;
            }
        }
        socket.leaveGroup(group);
        socket.close();
    }
}
