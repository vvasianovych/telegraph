package com.keebraa.telegraph.remote;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is test class that fetches requests via multicast group and answers with
 * expected payload. Can be used to send invalid responses.
 * 
 * @author vvasianovych
 *
 */
public class ExpectedPayloadTestMulticastClient extends SuccessTestMulticastClient {

    private String payload;

    public ExpectedPayloadTestMulticastClient(String microserviceName, String microserviceHost, int microservicePort, String multicastAddress, int port,
            String responseAddress, int responsePort, ObjectMapper objectMapper) throws IOException {
        super(microserviceName, microserviceHost, microservicePort, multicastAddress, port, responseAddress, responsePort, 10000, objectMapper);
    }

    public void setExpectedPayload(String payload) {
        this.payload = payload;
    }

    public String buildResponse() throws JsonProcessingException {
        return payload;
    }
}
