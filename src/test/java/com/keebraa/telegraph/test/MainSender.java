package com.keebraa.telegraph.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainSender {

    protected static byte[] buf = new byte[256];

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName("230.0.0.0");
        buf = "Hello world".getBytes();
 
        DatagramPacket packet 
          = new DatagramPacket(buf, buf.length, group, 4446);
        socket.send(packet);
        socket.close();
    }
}
