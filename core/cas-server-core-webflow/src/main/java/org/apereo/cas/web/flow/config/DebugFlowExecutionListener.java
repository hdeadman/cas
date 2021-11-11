package org.apereo.cas.web.flow.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.execution.EnterStateVetoException;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

/**
 * @author Hal Deadman
 * @since 6.4
 */
@Slf4j
@RequiredArgsConstructor
public class DebugFlowExecutionListener implements FlowExecutionListener {

    private static ThreadLocal<int[]> FLOW_COUNTER = new ThreadLocal<>();

    private final int flowExecutionMax;

    /**
     * Called when any client request is submitted to manipulate this flow execution. This call happens before request
     * processing.
     * @param context the current flow request context
     */
    public void requestSubmitted(final RequestContext context) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("requestSubmitted [{}]", context);
        }
    }

    /**
     * Called when a client request has completed processing.
     * @param context the source of the event
     */
    public void requestProcessed(final RequestContext context) {
        LOGGER.warn("Flow counter value at requestProcessed: [{}]", getFlowCounter());
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("requestProcessed [{}]", context);
        }
    }

    /**
     * Called to indicate a new flow definition session is about to be created. Called before the session is created. An
     * exception may be thrown from this method to veto the start operation. Any type of runtime exception can be used
     * for this purpose.
     * @param context the current flow request context
     * @param definition the flow for which a new session is starting
     */
    public void sessionCreating(final RequestContext context, final FlowDefinition definition) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("sessionCreating: definition = [{}], context = [{}]", definition, context);
        }
    }

    /**
     * Called after a new flow session has been created but before it starts. Useful for setting arbitrary attributes in
     * the session before the flow starts.
     * @param context the current flow request context
     * @param session the session that was created
     * @param input a mutable input map - attributes placed in this map are eligible for input mapping by the flow
     * definition at startup
     */
    public void sessionStarting(final RequestContext context, final FlowSession session, final MutableAttributeMap<?> input) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("sessionCreating: session = [{}], context = [{}]", session, context);
        }
    }

    /**
     * Called after a new flow session has started. At this point the flow's start state has been entered and any other
     * startup behaviors have been executed.
     * @param context the current flow request context
     * @param session the session that was started
     */
    public void sessionStarted(final RequestContext context, final FlowSession session) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("sessionStarted: flow id = [{}], session = [{}], context = [{}]", getFlowId(context), session, context);
        }
    }

    /**
     * Called when an event is signaled in the current state, but prior to any state transition.
     * @param context the current flow request context
     * @param event the event that occurred
     */
    public void eventSignaled(final RequestContext context, final Event event) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("eventSignaled: flow id = [{}], event = [{}], context = [{}]", getFlowId(context), event, context);
        }
    }

    /**
     * Called when a transition is matched but before the transition occurs.
     * @param context the current flow request context
     * @param transition the proposed transition
     */
    public void transitionExecuting(final RequestContext context, final TransitionDefinition transition) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("transitionExecuting: flow id = [{}], transition = [{}], context = [{}]", getFlowId(context), transition, context);
        }
    }

    /**
     * Called when a state transitions, after the transition is matched but before the transition occurs.
     * @param context the current flow request context
     * @param state the proposed state to transition to
     * @throws EnterStateVetoException when entering the state is not allowed
     */
    public void stateEntering(final RequestContext context, final StateDefinition state) throws EnterStateVetoException {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("stateEntering: flow id = [{}], state = [{}], context = [{}]", getFlowId(context), state, context);
        }
    }

    /**
     * Called when a state transitions, after the transition occurred.
     * @param context the current flow request context
     * @param previousState <i>from</i> state of the transition
     * @param state <i>to</i> state of the transition
     */
    public void stateEntered(final RequestContext context, final StateDefinition previousState, final StateDefinition state) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("stateEntering: flow id = [{}], state = [{}], context = [{}]", getFlowId(context), state, context);
        }
    }

    /**
     * Called when a view is about to render in a view-state, before any render actions are executed.
     * @param context the current flow request context
     * @param view the view that is about to render
     * @param viewState the current view state
     */
    public void viewRendering(final RequestContext context, final View view, final StateDefinition viewState) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("viewRendering: flow id = [{}], view = [{}], viewState = [{}], context = [{}]", getFlowId(context), view, viewState, context);
        }
    }

    /**
     * Called after a view has completed rendering.
     * @param context the current flow request context
     * @param view the view that rendered
     * @param viewState the current view state
     */
    public void viewRendered(final RequestContext context, final View view, final StateDefinition viewState) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("viewRendered: flow id = [{}], view = [{}], viewState = [{}], context = [{}]", getFlowId(context), view, viewState, context);
        }
    }

    /**
     * Called when a flow execution is paused, for instance when it is waiting for user input (after event processing).
     * @param context the current flow request context
     */
    public void paused(final RequestContext context) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("paused: id = [{}], context = [{}]", getFlowId(context), context);
        }
    }

    /**
     * Called after a flow execution is successfully reactivated after pause (but before event processing).
     * @param context the current flow request context
     */
    public void resuming(final RequestContext context) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("resuming: id = [{}], context = [{}]", getFlowId(context), context);
        }
    }

    /**
     * Called when the active flow execution session has been asked to end but before it has ended.
     * @param context the current flow request context
     * @param session the current active session that is ending
     * @param outcome the outcome reached by the ending session, generally the id of the terminating end-state
     * @param output the flow output produced by the ending session, this map may be modified by this listener to affect
     * the output returned
     */
    public void sessionEnding(final RequestContext context, final FlowSession session, final String outcome, final MutableAttributeMap<?> output) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("sessionEnding: flow id = [{}], session = [{}], outcome = [{}], output = [{}], context = [{}]", getFlowId(context), session, outcome, output, context);
        }
    }

    /**
     * Called when a flow execution session ends. If the ended session was the root session of the flow execution, the
     * entire flow execution also ends.
     * @param context the current flow request context
     * @param session ending flow session
     * @param outcome the outcome reached by the ended session, generally the id of the terminating end-state
     * @param output the flow output returned by the ending session
     */
    public void sessionEnded(final RequestContext context, final FlowSession session, final String outcome, final AttributeMap<?> output) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("sessionEnded: flow id = [{}], session = [{}], outcome = [{}], output = [{}], context = [{}]", getFlowId(context), session, outcome, output, context);
        }
    }

    /**
     * Called when an exception is thrown during a flow execution, before the exception is handled by any registered
     * {@link FlowExecutionExceptionHandler handler}.
     * @param context the current flow request context
     * @param exception the exception that occurred
     */
    public void exceptionThrown(final RequestContext context, final FlowExecutionException exception) {
        if (incrementFlowCounter() > flowExecutionMax) {
            LOGGER.warn("exceptionThrown: id = [{}], context = [{}], exception = [{}]", getFlowId(context), context, exception);
        }
    }

    private int incrementFlowCounter() {
        return FLOW_COUNTER.get() == null ? initFlowCounter() : FLOW_COUNTER.get()[0]++;
    }

    private int initFlowCounter() {
        if (FLOW_COUNTER.get() == null) {
            FLOW_COUNTER.set(new int[] {0});
        } else {
            FLOW_COUNTER.get()[0] = 0;
        }
        return 0;
    }

    private int getFlowCounter() {
        return FLOW_COUNTER.get()[0];
    }

    private String getFlowId(final RequestContext context) {
        try {
            return context.getActiveFlow().getId();
        } catch (final IllegalStateException e) {
            return "flow inactive";
        }
    }
}
