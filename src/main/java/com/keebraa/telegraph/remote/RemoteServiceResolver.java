package com.keebraa.telegraph.remote;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.now;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keebraa.telegraph.lib.MicroserviceDescriptor;
import com.keebraa.telegraph.lib.MulticastMicroserviceDescriptorRequest;
import com.keebraa.telegraph.lib.PoolMap;

/**
 * This resolver contains information about all available microservices and
 * holds its descriptors. Can resolve microservice based on name.
 * 
 * @author vvasianovych
 *
 */
public class RemoteServiceResolver {

    private static Logger log = LoggerFactory.getLogger("Telegraph");

    private static final int DEFAULT_THREAD_POOLSIZE = 5;

    private static final long DEFAULT_RESOLVE_TIMEOUT = 15000;

    private String multicastAddress;

    private int port;

    private ObjectMapper objectMapper;

    private String localAddress;

    private int localPort;

    private ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREAD_POOLSIZE);

    private Map<String, MicroserviceDescriptor> descriptors = new PoolMap(DEFAULT_RESOLVE_TIMEOUT);

    private ServerSocket serverSocket;

    public RemoteServiceResolver(String multicastAddress, int port, String localAddress, int localPort, ObjectMapper mapper) {
        validate(multicastAddress, "multicastAddress");
        validate(localAddress, "localAddress");

        this.multicastAddress = multicastAddress;
        this.port = port;
        this.objectMapper = mapper;
        this.localAddress = localAddress;
        this.localPort = localPort;
    }

    public void stop() throws InterruptedException, IOException {
        serverSocket.close();
        executor.shutdownNow();
        executor.awaitTermination(5, SECONDS);
    }

    /**
     * Runs server socket and reads responses from multicast group. This method just
     * accepts the connections and runs separated thread to resolve the response.
     * Each socket connection is just one Multicast response. So, each socket needs
     * to be just read.
     */
    public void handleResponses() {
        executor.execute(() -> {
            try {
                serverSocket = new ServerSocket(localPort);
                log.info("Start listening for a multicast responses. Port: {}", localPort);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executor.execute(() -> handleClientSocket(clientSocket));
                }
            } catch (SocketException exception) {
                log.error("Multicast response socket was closed Resolved is stopped.");
            } catch (Exception exception) {
                log.error("Server socket handling exception.", exception);
            }
        });
    }

    /**
     * The only one goal for this method is just read the socket input stream to get
     * response on multicast request. the answer should be Microservice descriptor.
     * 
     * @param clientSocket - accepted socket, connection from microservice that can
     *                     provide info about it's microservice
     */
    private void handleClientSocket(Socket clientSocket) {
        try {
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            String responseString = inputStream.readUTF();
            log.debug("Got answer for multicast request. Client: {}", clientSocket.getInetAddress());
            log.debug("Descriptor: {}", responseString);
            MicroserviceDescriptor descriptor = objectMapper.readValue(responseString, MicroserviceDescriptor.class);
            descriptor.setLastSynced(now().toEpochMilli());
            descriptors.put(descriptor.getName(), descriptor);
            inputStream.close();
            clientSocket.close();
        } catch (IOException exception) {
            log.error("Multicast response can't be processed.");
            log.debug("exception details:", exception);
        }
    }

    /**
     * Provides whether the cached microservice descriptor, or fetches new one in
     * case if it
     * 
     * @param microserviceName
     * @return
     * @throws IOException
     */
    public MicroserviceDescriptor resolveService(String microserviceName) throws IOException {
        long now = Instant.now().toEpochMilli();
        MicroserviceDescriptor descriptor = null;

        // Note that we should call 'contains', because out descriptors map will wait
        // for value in case of 'get'
        if (descriptors.containsKey(microserviceName)) {
            descriptor = descriptors.get(microserviceName);
        }

        // Check it's age.
        if (descriptor != null && now - descriptor.getLastSynced() < descriptor.getTtl()) {
            return descriptor;
        }

        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(multicastAddress);
        MulticastMicroserviceDescriptorRequest request = buildRequest(microserviceName);
        String payload = objectMapper.writeValueAsString(request);
        byte[] payloadBytes = payload.getBytes(UTF_8);
        DatagramPacket packet = new DatagramPacket(payloadBytes, payloadBytes.length, group, port);
        socket.send(packet);
        socket.close();
        descriptors.remove(microserviceName);
        return descriptors.get(microserviceName);
    }

    private void validate(String value, String valueName) {
        if (isBlank(value)) {
            log.error("Value '{}' can't be empty", valueName);
            throw new RuntimeException(String.format("Value %s can't be empty", valueName));
        }
    }

    private MulticastMicroserviceDescriptorRequest buildRequest(String microserviceName) {
        MulticastMicroserviceDescriptorRequest request = new MulticastMicroserviceDescriptorRequest();
        request.setMicroservices(asList(microserviceName));
        request.setRequesterHost(localAddress);
        request.setRequesterPort(localPort);
        request.setTimestamp(now().toEpochMilli());
        request.setRequestId(randomUUID().toString());
        return request;
    }
}
