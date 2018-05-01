package com.worthent.foundation.util.recorder.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worthent.foundation.util.recorder.DataErrorRecorder;
import com.worthent.foundation.util.recorder.Message;

/**
 * Records errors and associates them with the currently set data identifier.
 * 
 * @author Erik K. Worth
 * @version $Id: DataErrorRecorderImpl.java 52 2011-04-04 05:11:28Z eworth $
 */
public class DataErrorRecorderImpl implements DataErrorRecorder {

    /** Serial Version ID */
    private static final long serialVersionUID = 3741959118499577005L;

    /** Logger for this class */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DataErrorRecorderImpl.class);

    /** Identifies an empty list of messages */
    private static final List<Message> EMPTY_MSG_LIST = Collections.emptyList();

    /** The list of error messages recorded when the data ID is not set */
    private final List<Message> topLevel;

    /** The error messages grouped by data ID */
    private final Map<String, List<Message>> msgsByDataId;

    /** The current data ID used to associate reported errors with data */
    private String dataId;

    /** The currently selected message list based on the data ID */
    private List<Message> currentList;

    public DataErrorRecorderImpl() {
        dataId = null;
        topLevel = new LinkedList<Message>();
        msgsByDataId = new LinkedHashMap<String, List<Message>>();
        currentList = topLevel;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        appendMsgList("top-level", topLevel, buf);
        for (final Map.Entry<String, List<Message>> entry : msgsByDataId
                .entrySet()) {
            appendMsgList(entry.getKey(), entry.getValue(), buf);
        }
        return buf.toString();
    }

    @Override
    public String getDataId() {
        return dataId;
    }

    @Override
    public void setDataId(final String dataId) {
        if (!Objects.equals(dataId, this.dataId)) {
            this.dataId = dataId;
            if (null == dataId) {
                currentList = topLevel;
            } else {
                currentList = msgsByDataId.get(this.dataId);
            }
        }
    }

    @Override
    public void reportError(final String msg) {
        this.reportError(msg, null);
    }

    @Override
    public void reportError(String msg, Exception exc) {
        final Message message = new MessageImpl(msg, Message.Type.ERROR, exc);
        reportError(message);
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(msg, exc);
        }
    }

    @Override
    public int getErrorCount(final String dataId) {
        final List<Message> errsForDataId =
            (null == dataId) ? topLevel : msgsByDataId.get(dataId);
        return (null == errsForDataId) ? 0 : errsForDataId.size();
    }

    @Override
    public List<String> getErrorDataIds() {
        final List<String> errDataIds =
            new ArrayList<String>(msgsByDataId.size());
        // Gather by entry to make sure the data IDs are returned in order
        for (final Map.Entry<String, List<Message>> entry : msgsByDataId
            .entrySet()) {
            errDataIds.add(entry.getKey());
        }
        return errDataIds;
    }

    @Override
    public List<Message> getErrorMessages(final String dataId) {
        final List<Message> errsByDataId =
            (null == dataId) ? topLevel : msgsByDataId.get(dataId);
        return (null == errsByDataId) ? EMPTY_MSG_LIST : Collections
            .unmodifiableList(errsByDataId);
    }

    @Override
    public boolean hasErrors(final String dataId) {
        final List<Message> errsByDataId =
            (null == dataId) ? topLevel : msgsByDataId.get(dataId);
        return (null != errsByDataId) && !errsByDataId.isEmpty();
    }

    @Override
    public int getErrorCount() {
        int cnt = topLevel.size();
        for (final List<Message> errs : msgsByDataId.values()) {
            cnt = cnt + errs.size();
        }
        return cnt;
    }

    @Override
    public List<Message> getErrorMessages() {
        final List<Message> allErrs = new LinkedList<Message>(topLevel);
        for (final List<Message> errs : msgsByDataId.values()) {
            allErrs.addAll(errs);
        }
        return allErrs;
    }

    @Override
    public boolean hasErrors() {
        if (!topLevel.isEmpty()) {
            return true;
        }
        for (final List<Message> errs : msgsByDataId.values()) {
            if (!errs.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        dataId = null;
        topLevel.clear();
        msgsByDataId.clear();
        currentList = topLevel;
    }

    //
    // Helper Methods
    //

    /**
     * Adds a message to the error list associated with the current data ID.
     *
     * @param msg the message to add
     */
    private void reportError(final Message msg) {
        if (currentList == null) {
            currentList = new LinkedList<Message>();
            msgsByDataId.put(dataId, currentList);
        }
        if (!currentList.contains(msg)) {
            currentList.add(msg);
        }
    }

    private static void appendMsgList(
        final String dataId,
        final List<Message> msgs,
        final StringBuilder buf) {
        if ((null == msgs) || msgs.isEmpty()) {
            return;
        }
        buf.append("\n");
        buf.append(dataId);
        for (final Message msg : msgs) {
            buf.append("\n    ");
            buf.append(msg.getText());
            final Exception exc = msg.getException();
            if (null != exc) {
                final StringWriter excBuf = new StringWriter();
                final PrintWriter writer = new PrintWriter(excBuf);
                // This is intentional. This class records exceptions thrown at
                // a point where they cannot be allowed to interrupt the current
                // processing. They need to be preserved so that they can be
                // reported once the non-interruptible processing completes. The
                // stack trace is appended to the string representation so that
                // it can be properly logged. It is not exposed to the end user.
                exc.printStackTrace(writer);
                writer.flush();
                buf.append("\n");
                buf.append(excBuf.toString());
            }
        }
    }

}
