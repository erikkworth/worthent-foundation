package com.worthent.foundation.util.recorder;

import com.worthent.foundation.util.recorder.internal.DataErrorRecorderImpl;

/**
 * Factory for various types of error reporters and recorders.  There
 * are Error Reporters and Error Recorders.  Error Reporters only 
 * expose methods for reporting errors and have no methods for retrieving
 * the messages reported.  The Error Recorder interface extends the
 * Error Reporter interface to provide retrieval methods for the messages.
 * <p>
 * There is also a class of Data Error Reporters and Data Error Recorders.
 * These extend the other interfaces to allow clients to report and retrieve
 * errors based on a hierarchy.  The callers can push and pop names into
 * the hierarchy and the reported messages are associated with the given
 * level in the hierarchy.  These error reporters and recorders are useful
 * when writing out errors for an element in a hierarchy such as used to
 * validate a hierarchical data structure.  
 * 
 * @author Erik K. Worth
 */
public final class RecorderFactory {

    /**
     * @return a new error reporter
     */
    public static ErrorReporter newErrorReporter() {
        return new DataErrorRecorderImpl();
    }

    /**
     * @return a new error recorder
     */
    public static ErrorRecorder newErrorRecorder() {
        return new DataErrorRecorderImpl();
    }
    
    /**
     * @return a new data error reporter
     */
    public static DataErrorReporter newDataErrorReporter() {
        return new DataErrorRecorderImpl();
    }

    /**
     * @return a new data error recorder
     */
    public static DataErrorRecorder newDataErrorRecorder() {
        return new DataErrorRecorderImpl();
    }
    
    /** Hide the constructor. */
    private RecorderFactory() {
        // Empty
    }
}
