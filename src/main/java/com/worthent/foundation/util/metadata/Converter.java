package com.worthent.foundation.util.metadata;

import java.io.Serializable;

import com.worthent.foundation.util.recorder.DataErrorReporter;

/**
 * Specifies the method implemented by classes able to convert data from one
 * type to another.
 * 
 * @author Erik K. Worth
 */
public interface Converter extends Serializable {

    /**
     * Converts the input data of an expected type to a result of another type.
     * 
     * @param data input data
     * @param errRecorder object used to report conversion errors
     * @return the output data
     */
    Object convert(Object data, DataErrorReporter errRecorder);
}
