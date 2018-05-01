package com.worthent.foundation.util.metadata;

import java.io.Serializable;

import com.worthent.foundation.util.recorder.DataErrorReporter;

/**
 * Specifies the methods implemented by all validators. Validators perform
 * specific checks on data and record errors for data that is invalid.
 * 
 * @author Erik K. Worth
 */
public interface Validator extends Serializable {

    /**
     * Implementations of this method validate the specified data against some
     * set of constraints and report errors to the provided data error reporter.
     * Callers are presumed to have set the data ID in the data error reporter
     * so that any errors reported are associated with that data ID.
     * 
     * @param data the data to be validated
     * @param errReporter the error reporter used to associated the errors with
     *        some path or key for the data being validated
     */
    void validate(Object data, DataErrorReporter errReporter);
}
