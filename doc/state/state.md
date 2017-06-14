# State Table Utility

The State Table Utility makes it easy to implement state machines that do work as they transition between discrete states based on events.

State machines are good for a few different kinds of use cases that are all naturally driven by events.  Over the course of my career I have implemented state tables to support

* an implementation of the Kerberos authentication algorithm
* an order management system for a pharmaceutical equipment market place
* a terminal emulator
* an XML Parser based on the SAX Parser

They can serve will in software systems that control mechanical devices and in controllers supporting an interactive user interface.  They also work well in parsers as you will see below.

Because state tables employ a higher level of abstraction than common procedural code, they can make highly event-driven and asynchronous software more reliable (with fewer defects).  Perhaps the biggest downside is that the developer needs to think a little differently when creating solutions that use state tables.

When you are writing code where there exists in the underlying domain some set of natural states for the system and you find you have a lot of nested `if` statements, then you might consider employing a state table approach to make sure you are not missing some condition in some state.  This approach makes it easier to make sure you are covering all your bases.

## Turnstile State Table Example

Perhaps the best way to explain what state tables can do is by presenting a simple example.  Let's take a look at how a state table might be used to control a turnstile like the kind you find at the zoo.

The example is organized to explain these topics:

* [Enumerate the States](#enumerate-the-states)
* [Define Events](#define-events)
* [Implement the State Table Data Object](#implement-the-state-table-data-object)
* [State Table Data Manager](#state-table-data-manager)
* [Transition Actors](#transition-actors)
* [State Table Definition](#state-table-definition)
* [State Table Controller](#state-table-controller)

**Turnstile**

![Turnstile](turnstile.jpg)

The above turnstile is a modern one at the park entrance for my favorite zoo.  When the attendant scans your ticket, the bars will turn (the three rotate together from the point where they all extend out) to allow one person to push through.

State Machines are often modeled using state transition diagrams that use ovals to represent each of the states in which a system can exist, and connect the states using arrows where the arrows represent the transition from one state to another (in the direction of the arrow) when a specific event arrives.  Software implementations of a state table typically can execute some procedures as the state machine transitions from one state to another (thus, they are said to do work on the state transition).  Here is a state table diagram for the turnstile.

**Turnstile State Transition Diagram**

![Turnstile](TurnstileStateDiagram.png)

State Tables typically have a starting state.  Here the reasonable starting state is for the machine to be turned off.  When in the Off state the turnstile can be turned on with an On event.  Presumably this event would be emitted to the State Table when the machine was powered on.

When the turnstile state table receives the On event while it is in the Off state, the state table transitions to the Locked state.  While in the Off or Locked state, the turnstile does not turn.  The Push event is signaled to the state table when a person tries to push through the turnstile.  If the Push event is signaled to the state table while it is in the Locked state, the turnstile does not turn and the state table stays in the same state (Locked).  In the diagram, a transition that keeps the state table in the same state is represented by an arrow that circles back to the same state.

In order for a person to get through the turnstile, the attendant needs to scan a ticket while the state table is in the Locked state.  When the ticket is scanned, the device signals a Ticket event to the state table.  When the Ticket event arrives while the state table is in the Locked state, the state table transitions to the Unlocked state.  The \[Increment Count\] on the diagram below the Ticket event signifies that the **Increment Count** Transition Actor is invoked as the state table transitions from the Locked to the Unlocked state.  Now that the state table is in the Unlocked state, the person will be able to push through.  The Push event triggers the state table to transition back to the Locked state until the next ticket scan.

A **Transition Actor** is a piece of code that is configured to run during the state transition.  A given Transition Actor can be used in multiple places in the state table and you can see from the diagram that it is indeed used in two places (one with the Ticket event and the other with the Push event).  The configuration for a state transition between two states can list more than one and they run in the order they are listed.

So now you have an overview of the concept, so let's break down the machine into its parts.  At the high level, the state table has these parts:

* the state table definition (identifying the states, transition between states on events, and the transition actors that do work)
* the data on which the state table works (that minimally identifies the state the state table is in at any given time)
* a data manager that is responsible for initializing the data to be operated on, fetching it on the arrival of an event, and saving it back when the transition complete
* the state table controller used to manage its life cycle (start and stop) and to feed it events
* the basic state transition engine that orchestrates processing of an event through a state transition
* a state transitioning component that is called after every successful state transition (that can do whatever you need it to do)
* an error handler component that is called whenever there is an error during the processing of an event

That is a lot of parts, so there is a `StateTableBuilder` to make it easy to put them all together for your application.  You will see how to use the builder below, but first let's have a look at how to define the states, events and data object.

### Enumerate the States

I like to use an enumerated type to enumerate all the states in the state table.  Here is the enumeration for the turnstile states.
```java
/** The states in which the coin-operated turnstile can exist */
public enum TurnstileStates {
    OFF,
    LOCKED,
    UNLOCKED
}
```

For the most flexibility, the state table definition uses Strings to identify states, but it is nice to constrain them to avoid typos using an enumerated type.

### Define Events

The events that you feed into the state table all need to implement the following interface:
```java
public interface StateEvent {
    /** Returns the string identifier for this event */
    String getName();
} // StateEvent
```

They all have a String name.  The name is used in the state table definition to identify the transition to take in each state.  Events typically carry other data that is acted on when the event is processed by the Transition Actors.

You can also use an enumerated type to enumerate all the names of the events that are processed by your state table.  Here is the enumerated type for the turnstile events:
```java
public enum TurnstileEventType {
    /** Turn on the turnstile */
    ON,

    /** The user pushes the turnstile arms */
    PUSH,

    /** The attendant scans a ticket */
    TICKET,

    /** Turn off the turnstile */
    OFF
}
```

For simple state tables like the turnstile, your events may not carry any data, and so you can use a helper class to create constants for your events like this:
```java
    private static final StateEvent ON_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.ON);
    private static final StateEvent PUSH_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.PUSH);
    private static final StateEvent TICKET_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.TICKET);
    private static final StateEvent OFF_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.OFF);
```
There are some other helper classes that implement the State Event interface that you can use if your events carry data.  You can use the `StateEvents.builder(String eventName)` to get a builder that will build an event with data carried in a backing map.  You can also implement your own classes.

### Implement the State Table Data Object

Most of the logic that you provide is encapsulated in the State Table Data object.  You need to implement a class that implements `StateTableData`:
```java
public interface StateTableData {

    /** Returns the current state of the state table instance */
    String getCurrentState();

    /** Sets the current state of the state table instance */
    void setCurrentState(String currentState);

    /** Returns the prior state of the state table instance */
    String getPriorState();

    /** Sets the prior state of the state table instance */
    void setPriorState(String priorState);

}
```
You can see that the interface provides for the management of the current state and the prior state.  In most cases you will want to extend the `AbstractStateTableData` class that provides a simple abstract implementation of this interface.

Here is the State Table Data object for the turnstile:
```java
import com.worthent.foundation.util.state.AbstractStateTableData;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.TransitionContext;
import com.worthent.foundation.util.state.annotation.Actor;

/** The state table data object for the turnstile */
public class TurnstileData extends AbstractStateTableData {

    /** The name of the actor that increments the turn and ticket counts */
    public static final String INCREMENT_COUNT = "incrementCount";

    /** The number of people that passed through the turnstile */
    private int turnCount;
    /** The number of tickets scanned for the turnstile */
    private int ticketCount;

    /** Construct in initial state */
    public TurnstileData() {
        super(TurnstileStates.OFF.name(), TurnstileStates.OFF.name());
        turnCount = 0;
        ticketCount = 0;
    }

    /** Copy constructor */
    public TurnstileData(final TurnstileData other) {
        super(other);
        this.turnCount = other.getTurnCount();
        this.ticketCount = other.getTicketCount();
    }

    /** Set from other state */
    public void set(final TurnstileData other) {
        super.set(other);
        this.turnCount = other.getTurnCount();
        this.ticketCount = other.getTicketCount();
    }

    /** Actor used to increment a counter based on the event type */
    @Actor(name = INCREMENT_COUNT)
    public void increment(final TransitionContext<TurnstileData, StateEvent> context) {
        final String eventName = context.getEvent().getName();
        if (TurnstileEventType.PUSH.name().equals(eventName)) {
            turnCount++;
        } else if (TurnstileEventType.TICKET.name().equals(eventName)) {
            ticketCount++;
        }
    }

    /** Returns the current turnCount */
    public int getTurnCount() {
        return turnCount;
    }

    /** Returns the current ticket count */
    public int getTicketCount() { return ticketCount; }
}
```

### State Table Data Manager

Since state tables can be used to solve very different kinds of problems, we need to support different ways to manage the data.  As you will see in the example a little later that the State Table Data Manager is able to perform three operations on your data object:

* initialize: you provide the logic to perform any initialization - this is invoked only once and not per event
* get: you provide the getter that is able to fetch the data
* set: you provide the setter that is able to set the data when the transition completes

Here is the interface that specifies the `StateTableDataManager`:
```java
public interface StateTableDataManager<D extends StateTableData, E extends StateEvent> {
    /**
     * Sets the current state in the state table data object to the initial
     * state of the state table. This is typically called from the
     * implementation of the {@link StateTableControl#start()} method.
     *
     * @throws StateDefException thrown when there is an error initializing the
     *             state table
     */
    void initializeStateTableData() throws StateDefException;

    /**
     * Returns a reference to the state table data object that minimally holds the
     * current and prior states of the state table instance. This method is
     * called by the engine when the event processing begins for the specified
     * event. This method is often implemented to return a copy of the data that
     * is then modified by the state transition actors. If the processing completes
     * successfully, the engine sets the updated copy back into this state table via
     * a call to {@link #setStateTableData(StateEvent, StateTableData)}.
     *
     * @param event the event being processed
     *
     * @throws StateExeException thrown when there is an error retrieving the
     *             state history
     */
    D getStateTableData(E event) throws StateExeException;

    /**
     * Updates the state table instance with a new value of the data object that
     * minimally holds the current and prior states of the state table instance.
     * This method is called by the engine when the processing has completed
     * successfully for the event. The data object passed in here is the updated
     * copy modified by the state transition actors.
     *
     * @param event the event that triggered the state table to update its data
     * @param dataObject the new data object to set into the state table
     *            instance
     *
     * @throws StateExeException thrown when there is an error setting the state
     *             history
     */
    void setStateTableData(E event, D dataObject) throws StateExeException;
}
```
The builder provides an implementation of this class that uses lambdas so that you can specify the each of the operations directly in the builder, but you can also provide your own implementation of the data manager and set that into the State Table using the builder.

Your state table may not require special implementations for all three of the data management operations depending on your data management needs, but you will minimally need to provide the get operation to the builder (it provides no-op operations for the others when left out).  With the three operations available to you, you can provide code that:

* reads the data from a database, creates a default instance when there is no data yet, writes the updated data back to the database if and only if all the Transition Actors succeed
* reads the data from a class variable and lets the Transition Actors make changes directly to the single copy
* creates a new instance of the data when it does not exist, or creates a copy of the data when it does, and replaces the value of the class variable when all the Transition Actors succeed

The options you provide depend on the requirements you have to support transactions and atomic data updates.

The `TurnstileData` object above supports atomic updates by providing:

* initialize: calls the default constructor to set an instance variable to a data object value with the current and prior states set to the Off state
* get: a copy constructor that creates a copy of the data to preserve the original data in case there is an error
* set: a set method that uses the data in the copy to update the original value (the source of truth)

The turnstile state table always works on a copy of the data.  Only when the transition completes successfully does the state table set the state table data.  If there is an error, the state is left in the original state (before the event) and the rest of the data remains unchanged.  This is probably overkill for this specific example, but I wanted to show all the parts to give you an idea of what is available.  More real-time state tables will not copy the data for every event and just work with the same instance of the data (and only provide the getter).

### Transition Actors

Please take note of the `increment` method in the `TurnstileData` object with the `@Actor` tag on it.  This is the easiest way to create the code for a Transition Actor that does work during state transitions.  The `TransitionActor` specifies the interface that all Transition Actors implement:

```java
public interface TransitionActor<D extends StateTableData, E extends StateEvent> {

    /** The default name of the actor when no name is provided */
    String UNNAMED = "UNNAMED_ACTOR";

    /**
     * Returns the name of the StateActor for logging purposes.
     */
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
    void onAction(TransitionContext<D, E> context) throws StateExeException;
```

The State Table Builder looks for `@Actor` tags and creates an implementation of the `TransitionActor` for you where the `onAction` method calls the method you tagged during a state transition.  The State Table Engine creates a `TransitionContext` with a bunch of information in it that is available to your Actor method during the state transition.  The `TransitionContext` gives your code access to:

* the event that triggered the state transition
* the state the state table was in when the event arrived
* the state the state table will end up if there is no error
* the state table control object in case you want to inject another event in as your are processing this one
* the state table data (in the case where your actor is a static method or some helper class) and you need to do something with the data

You can see that the `increment` method in the `TurnstileData` object takes the TransitionContext as a parameter.  In most cases when you tag a method on your data object as a Transition Actor, you will only need to reference the event - the Transition Actor implementation will extract the event from the context and just pass that when it sees your method takes the event type as an argument.

The only Transition Actor in the `TurnstileData` simply increments a count for the event.  You can imagine that the park operations would want to know how many tickets were scanned and how many people went though as a means to audit their attendance numbers, so the class has accessors for the two counts.  In reality this example is probably too simple for a real park, but you can imagine that it would probably really need to invoke some methods to actually lock and unlock the turnstile mechanisms and would probably store the data persistently, but that is beyond the scope of an example.

### State Table Definition

Now it might be helpful to refer back to the State Transition Diagram above to recall the states and transitions for the turnstile so you can see how the diagram maps to the construction of the state table definition using the builder.  Here is how you create the turnstile state table definition along with the state table data as a class instance variable:

```java
/** State table data */
private TurnstileData stateTableData;

/** State table representing a turnstile like you find in amusement parks */
private final StateTable<TurnstileData, StateEvent> turnstileStateTable =
    new StateTableBuilderImpl<TurnstileData, StateEvent>()
        .withStateTableDefinition()
            .setName("Turnstile")
            .usingActorsInClass(TurnstileData.class)
            .withState(TurnstileStates.OFF.name())
                .transitionOnEvent(TurnstileEventType.ON.name()).toState(TurnstileStates.LOCKED.name()).endTransition()
                .withDefaultEventHandler().toState(StateDef.STAY_IN_STATE).endTransition()
                .endState()
            .withState(TurnstileStates.LOCKED.name())
                .transitionOnEvent(TurnstileEventType.TICKET.name())
                    .toState(TurnstileStates.UNLOCKED.name())
                    .withActorsByName(TurnstileData.INCREMENT_COUNT)
                    .endTransition()
                .transitionOnEvent(TurnstileEventType.PUSH.name()).toState(StateDef.STAY_IN_STATE).endTransition()
                .transitionOnEvent(TurnstileEventType.OFF.name()).toState(TurnstileStates.OFF.name()).endTransition()
                .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                .endState()
            .withState(TurnstileStates.UNLOCKED.name())
                .transitionOnEvent(TurnstileEventType.TICKET.name()).toState(StateDef.STAY_IN_STATE).endTransition()
                .transitionOnEvent(TurnstileEventType.PUSH.name())
                    .toState(TurnstileStates.LOCKED.name())
                    .withActorsByName(TurnstileData.INCREMENT_COUNT)
                    .endTransition()
                .transitionOnEvent(TurnstileEventType.OFF.name()).toState(TurnstileStates.OFF.name()).endTransition()
                .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                .endState()
            .endDefinition()
        .withStateTableDataManager()
            .withInitializer(() -> stateTableData = new TurnstileData())
            .withDataGetter((e) -> new TurnstileData(stateTableData))
            .withDataSetter((e, updatedData) -> stateTableData.set(updatedData))
            .endDataManager()
        .build();
```

The state table definition is built using a collection of builders for the various parts in a fluid way.  State table definitions are immutable once built.  They are defined with two generic arguments: one for the concrete data type and the other for the event type (most non-trivial state tables do not use the base `StateEvent` but a more derived class for the event type).

The indentation is intended to make the definition easier to read.  After the creating the builder, the first level of indentation specifies the state table definition and the data manager (respectively).  Within the state table definition, the next level of indentation describes the top-level aspects of the state table definition:

* the state table name: "turnstile"
* the instruction to look for Transition Actor tags in the `TurnstileData` class
* state definitions (the ovals in the State Transition Diagram)

Each state definition is indented to identify the state transitions.  Each of the transition definitions represents one of the arrows between the states in the diagram.  Each transition definition identifies:

* the event that will trigger the transition
* the state to go to when the transition completes successfully
* the actors that are to be called in the order listed to transform the data or do any other work required

Each state has a default transition handler that is called when the received event does not identify a specific transition to take.  In most cases this is handled as an error, but you might also just ignore it (but that is often not a good practice).

The above builder code has the State Table Data Manager definition at the bottom.  You can see how the lambdas there initialize the `stateTableData` instance variable using the default constructor, get a copy of the data using the copy constructor, and set the modified copy of the data back into the instance variable.

### State Table Controller

Once the State Table is built it is immutable and may be used over and over again with different data and events.  You can now create an instance of the State Table Controller, start it up, send events and shut it down.  Here is the specification for the `StateTableControl` component:
```java
public interface StateTableControl<E extends StateEvent> {

    /**
     * Starts the state transition engine.
     *
     * @throws StateExeException thrown when there is an error starting the
     *         state table transition engine
     */
    void start() throws StateExeException;

    /**
     * Directs the state transition engine to stop. It may not stop until all of
     * the currently queued events are processed.
     *
     * @throws StateExeException thrown when there is an error stopping the
     *         state table transition engine
     */
    void stop() throws StateExeException;

    /**
     * Signals an event to the state transition engine. The event is queued for
     * the state engine. Events are processed in the order they are received.
     *
     * @throws StateExeException thrown when there is an error signalling an
     *         event
     */
    void signalEvent(E event) throws StateExeException;
}
```

You can see that the `StateTableControl` object takes a generic argument to specify the concrete event type.  It provides some life cycle management methods to start and stop the component.  These methods are provided so they can start and stop threads in a thread pool for the implementations that use threads.  Then there is the method that submits events to the state table.

There are (or will be) multiple implementations of the `StateTableControl` that differ primarily by their threading models:

* `SerialStateTableControl`: This is a thread-unsafe implementation that is meant to be constructed and used within the processing of some high-level request all in the same thread.  It does not start a thread.  Any exceptions thrown during the state table execution propagate up through the method that signals the event.  This is a good candidate for more real-time processing and for parsers.
* `SingleThreadConsumerStateTableControl` (not yet implemented in v1.0): This implementation is thread safe and directs all events to a queue that is consumed by a single thread that feeds the events to the state table in the order received (with the exception of events submitted by an actor).  This version is good for processing transactional events where order is important but throughput is less important.
* `MultiThreadedConsumerStateTableControl` (not yet implemented in v1.0): This implementation is thread safe and uses a hash algorithm to dispatch a value from the event (a data ID) to one of multiple queues each of which have their own thread consuming events and feeding them into the state table.  This provides a higher throughput capacity while preserving the order for events with the same data ID.

The `SerialStateTableControl` objects invokes the initializer on the State Table Data Manager.

All of them (will) use the same underlying `StateEngine` implementation that process the transition on an event.  The differences for how the events are queued, consumed by threads from a thread pool, and feed into the `StateEngine`, are all encapsulated in the implementation of the `StateTableControl`.

The `StateEngine` processes a single event submitted by the `StateTableControl` following these steps:

1. Retrieve the State Table Definition from the State Table
1. Retrieve the State Table Data object using the getter from the State Table Data Manager passing the event as an argument
1. Reads the current state from the State Table Data
1. Uses the current state to get the State definition from the State Table Definition
1. Uses the event name from the State Event to lookup the State Transition Definition from the State Definition
1. Determines the target state from the State Transition Definition
1. Creates a Transition Context object with the following data:
   * Current State
   * Target State
   * State Table
   * State Table Data
   * State Table Control
   * Event
1. Get the list of Transition Actors
1. Invoke each Transition Actor in order
1. Get the State Transitioner from the State Table and invoke it if not `null`
1. Call the setter on the State Table Data Manager to set the updated data

The turnstile state table uses the `SerialStateTableControl` to process events.  Create an instance of it using the constructor and pass the state table definition as an argument.  That's it:
```java
    private StateTableControl<StateEvent> stateTableController;

    . . .

    stateTableController = new SerialStateTableControl<>(turnstileStateTable);
```

Here is how you feed it events to turn it on, scan a ticket to unlock, push through to lock again, and turn it off:
```java
        stateTableController.start();
        stateTableController.signalEvent(ON_EVENT);
        stateTableController.signalEvent(TICKET_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);
        stateTableController.signalEvent(OFF_EVENT);
```

You can get the push and ticket counts from the data object at the end.  I have a unit test that does this:
```java
        assertEquals("Expected Turn Count", 1, stateTableData.getTurnCount());
        assertEquals("Expected Ticket Count", 1, stateTableData.getTicketCount());
```

In this example you have seen how to create a state table and feed it events.  Here are the programming tasks as a recap:

1. [Enumerate the States](#enumerate-the-states)
1. [Define Events](#define-events)
1. [Implement the State Table Data Object (with Actors)](#implement-the-state-table-data-object)
1. [Define the State Table Using the Builder](#state-table-definition)
1. [Construct the State Table Controller](#state-table-controller)

Next, I will present a more complex (and useful) example that parses XML documents to create Java Data Transfer Objects.
