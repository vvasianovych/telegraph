package com.keebraa.telegraph.exception;

import static java.lang.String.format;

/**
 * Represents any communication issues, including parsing of response
 * 
 * @author vvasianovych
 *
 */
public class CommunicationException extends RuntimeException {

    public CommunicationException(String msName, String details) {
        super(format("Can't connect remote Microservice. MS name: '%s', details: %s", msName, details));
    }
}
