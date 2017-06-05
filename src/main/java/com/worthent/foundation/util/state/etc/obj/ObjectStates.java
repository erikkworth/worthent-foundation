package com.worthent.foundation.util.state.etc.obj;

/**
 * Enumerates the states in the state table used to build objects
 * @author Erik K. Worth
 */
public enum ObjectStates {

    /** The state waiting for the object building to begin */
    AWAITING_ROOT_START,

    /** The state waiting an Entity Start event */
    AWAITING_ENTITY_START,

    /** The state where values are set on started objects */
    BUILDING_ENTITY,

    /** The state where a list is being built */
    BUILDING_LIST;
}
