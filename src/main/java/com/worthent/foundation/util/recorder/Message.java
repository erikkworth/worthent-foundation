package com.worthent.foundation.util.recorder;

import java.io.Serializable;

/**
 * To hold the details about a message
 * 
 * @author Erik K. Worth
 */
public interface Message extends Serializable {

    /** Definition of message types */
    public enum Type {
        ERROR, WARNING, INFO
    };

    /**
     * @return the message the text
     */
    String getText();

    /**
     * @return the message type
     */
    Type getMessageType();
    
    /**
     * @return the exception caught and recorded or <code>null</code> if no
     * exception was caught
     */
    Exception getException();

}
