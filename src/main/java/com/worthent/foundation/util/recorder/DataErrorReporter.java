package com.worthent.foundation.util.recorder;

/**
 * Specifies the methods implemented by classes that allow clients to report
 * errors against invalid data by associating the reported errors with a data
 * identifier such as a path or key to the data.  This interface extends 
 * <code>ErrorReporter</code> to allow clients to set and get a data ID.  Each
 * error reported is associated with the currently set data ID.
 * 
 * @author Erik K. Worth
 */
public interface DataErrorReporter extends ErrorReporter {

    /** @return the data identifier for which errors are currently reported */
    String getDataId();
    
    /**
     * Sets the data identifier for which errors are to be reported
     *
     * @param dataId the data identifier
     */
    void setDataId(String dataId);
}
