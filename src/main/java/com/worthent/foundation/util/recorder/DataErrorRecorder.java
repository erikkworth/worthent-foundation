package com.worthent.foundation.util.recorder;

import java.io.Serializable;
import java.util.List;

/**
 * Specifies methods for reporting errors associated with data fields that may
 * be retrieved.
 * 
 * @author Erik K. Worth
 */
public interface DataErrorRecorder extends DataErrorReporter, ErrorRecorder, Serializable {

    /** @return the list of data IDs for which errors have been recorded */
    List<String> getErrorDataIds();

    /**
     * Returns <code>true</code> when there are errors recorded
     *
     * @param dataId identifies the data item that has an error
     * @return <code>true</code> when there are errors recorded
     */
    boolean hasErrors(String dataId);

    /**
     * Returns the number of error messages
     *
     * @param dataId identifies the data item with errors
     * @return the number of error messages
     */
    int getErrorCount(String dataId);

    /**
     * Returns a list of all recorded error messages for the specified data ID
     *
     * @param dataId identifies the data item with errors
     * @return a list of all recorded error messages for the specified data ID
     */
    List<Message> getErrorMessages(String dataId);

    /** Reset the error recorder back to its initial empty state */
    void clear();
}
