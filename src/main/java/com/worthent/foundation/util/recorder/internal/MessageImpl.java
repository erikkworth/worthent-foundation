package com.worthent.foundation.util.recorder.internal;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.worthent.foundation.util.recorder.Message;

/**
 * To hold the details about a message
 * 
 * @author Erik K. Worth
 */
public class MessageImpl implements Message {

    /** Serial Version ID */
    private static final long serialVersionUID = -4246598008669972459L;

    /** Text of the message */
    private final String text;

    /** Type of message */
    private final Type messageType;

    /** Exception that was caught */
    private transient Exception caught;
    
    /** Used for internalization purposes only */
    @SuppressWarnings("unused")
    private MessageImpl() {
        text = null;
        messageType = null;
    }

    /**
     * Constructs a new Message with the given text and the type of the message
     * 
     * @param text the message text to be set
     * @param messageType the type of the message
     */
    public MessageImpl(final String text, final Type messageType) {
        this(text, messageType, null);
    }

    /**
     * Constructs a new Message with the given text and the type of the message
     * 
     * @param text the message text to be set
     * @param messageType the type of the message
     * @param caught exception caught
     */
    public MessageImpl(
        final String text,
        final Type messageType,
        final Exception caught) {
        if (null == text) {
            throw new IllegalArgumentException("text must not be null");
        }
        if (null == messageType) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        this.text = text;
        this.messageType = messageType;
        this.caught = caught;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the messageType
     */
    public Type getMessageType() {
        return messageType;
    }
    
    /**
     * @return the exception caught and recorded or <code>null</code> if no
     * exception was caught
     */
    public Exception getException() {
        return caught;
    }

    /**
     * Return a string representation of the message.
     */
    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        final PrintWriter sb = new PrintWriter(writer);
        sb.append("[" + getMessageType() + "]");
        sb.append(" " + getText());
        if (null != caught) {
            sb.append("\n");
            // This is intentional.  This class records exceptions thrown at
            // a point where they cannot be allowed to interrupt the current
            // processing.  They need to be preserved so that they can be
            // reported once the non-interruptible processing completes.  The
            // stack trace is appended to the string representation so that it
            // can be properly logged.  It is not exposed to the end user.
            caught.printStackTrace(sb);
        }
        sb.flush();
        return writer.toString();
    }
}
