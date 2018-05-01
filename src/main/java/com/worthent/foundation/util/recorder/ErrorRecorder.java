package com.worthent.foundation.util.recorder;

import java.util.List;

/**
 * Specifies methods supported on object that record reported errors.  The
 * recorded errors may be retrieved.
 * 
 * @author Erik K. Worth
 */
public interface ErrorRecorder extends ErrorReporter {

    /** @return a list of all recorded error messages */
    List<Message> getErrorMessages();
}
