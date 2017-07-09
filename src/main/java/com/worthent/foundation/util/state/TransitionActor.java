package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;

/**
 * Specifies methods implemented by objects that perform actions during state
 * transitions.
 * 
 * @author Erik K. Worth
 * @version $Id: TransitionActor.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public interface TransitionActor<D extends StateTableData, E extends StateEvent> {

    /** The default name of the actor when no name is provided */
    String UNNAMED = "UNNAMED_ACTOR";

    /**
     * @return the name of the Transition Actor.
     */
    @NotNull
    default String getName() {return UNNAMED;}

    /**
     * This method is called to take action on a state transition.
     * <p>
     * Concrete instances of this interface are created and inserted into the
     * table before the table is initialized.
     * 
     * @param context the transition context from the current state to the next
     *
     * @exception StateExeException thrown when the onAction method fails (it
     *            prevents the state transition and subsequent action methods
     *            from being invoked)
     */
    void onAction(@NotNull TransitionContext<D, E> context) throws StateExeException;

}
