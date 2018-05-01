package com.worthent.foundation.util.recorder;

/**
 * Specifies the methods implemented by classes that allow clients to report
 * errors.
 * 
 * @author Erik K. Worth
 */
public interface ErrorReporter {
    
    /**
     * Reports the specified message as an error
     * 
     * @param msg the error message
     */
    void reportError(String msg);
    
    /**
     * Reports the specified message and exception caught as an error
     * 
     * @param msg the error message
     * @param exc the exception caught
     */
    void reportError(String msg, Exception exc);

    /** @return <code>true</code> when there are errors recorded */
    boolean hasErrors();
    
    /** @return the number of error messages */
    int getErrorCount();
    
}
