package com.keebraa.telegraph.lib;

/**
 * Represents three possible scenarios of remote method call
 * 
 * @author vvasianovych
 *
 */
public enum CallStatus {

    // Success means that method call result contains result of the remote service
    // call, and remote method was called successfully.
    SUCCESS,

    // Error means that remote method threw some exception during call
    ERROR,

    // Means that the needed method was not found on remote side.
    NOT_FOUND;
}
