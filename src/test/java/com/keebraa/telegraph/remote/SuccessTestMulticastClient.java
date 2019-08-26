package com.keebraa.telegraph.remote;

import static java.net.InetAddress.getByName;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keebraa.telegraph.lib.MicroserviceDescriptor;
import com.keebraa.telegraph.lib.MulticastMicroserviceDescriptorRequest;

/**
 * This is test class that fetches requests via multicast group and answers with
 * expected response
 * 
 * @author vvasianovych
 *
 */
public class SuccessTestMulticastClient {

    private static Logger log = LoggerFactory.getLogger("TestMSClient");

    private MulticastSocket socket;

    private InetAddress group;

    private Thread readThread;

    private String microserviceName;

    private String responseAddress;

    private int responsePort;

    private String microserviceHost;

    private int microservicePort;

    private ObjectMapper objectMapper;

    private long timeout;

    public SuccessTestMulticastClient(String microserviceName, String microserviceHost, int microservicePort, String multicastAddress, int port,
            String responseAddress, int responsePort, ObjectMapper objectMapper) throws IOException {

        this.microserviceName = microserviceName;
        this.responseAddress = responseAddress;
        this.responsePort = responsePort;
        this.microserviceHost = microserviceHost;
        this.microservicePort = microservicePort;

        this.objectMapper = objectMapper;

        socket = new MulticastSocket(port);
        group = getByName(multicastAddress);
        socket.joinGroup(group);
    }

    public void setResponseTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void run() {
        readThread = new Thread(() -> {
            byte[] buffer = new byte[10240];
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                MulticastMicroserviceDescriptorRequest request = objectMapper.readValue(received, MulticastMicroserviceDescriptorRequest.class);
                if (request == null || !request.getMicroservices().contains(microserviceName)) {
                    return;
                }

                Thread.sleep(timeout);
                Socket responseSocket = new Socket(responseAddress, responsePort);
                DataOutputStream stream = new DataOutputStream(responseSocket.getOutputStream());
                stream.writeUTF(buildResponse());
                responseSocket.close();
            } catch (IOException | InterruptedException e) {
                log.error("exception", e);
            }
        });
        readThread.start();
    }

    public void stop() throws IOException {
        socket.leaveGroup(group);
        socket.close();
    }

    public String buildResponse() throws JsonProcessingException {
        MicroserviceDescriptor descriptor = new MicroserviceDescriptor();
        descriptor.setName(microserviceName);
        descriptor.setHost(microserviceHost);
        descriptor.setPort(microservicePort);
        return objectMapper.writeValueAsString(descriptor);
    }
}
