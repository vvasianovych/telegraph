package com.keebraa.telegraph.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MainSender {

    protected static byte[] buf = new byte[256];

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("192.168.1.27", 9992);
 
        DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
        stream.writeUTF("{\"name\":\"testMS\", \"host\":\"192.168.1.10\", \"port\":9993, \"pubKey\": null, \"ttl\": 0, \"serviceDescriptors\" : []}");
        stream.close();
        socket.close();
    }
}
