package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.StateTransition;
import dev.workforge.app.WorkForge.Model.Workflow;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class WorkflowFactory {
    private final Map<Long, StateTransitionGroup> stateTransitionMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long expirationTimeMillis = 30 * 60 * 1000;

    public WorkflowFactory() {
        startCleanupTask();
    }

    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(this::cleanupExpiredGroups, 1, 30, TimeUnit.MINUTES);
    }

    private void cleanupExpiredGroups() {
        if (stateTransitionMap.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<Long, StateTransitionGroup>> entryIterator = stateTransitionMap.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Long, StateTransitionGroup> entry = entryIterator.next();
            StateTransitionGroup stateTransitionGroup = entry.getValue();

            if (stateTransitionGroup.isExpired()) {
                entryIterator.remove();
            }
        }
    }

    private class StateTransitionGroup {
        private State fromState;
        private List<State> toStates;
        private long lastAccessTime;
        private final long expirationTimeMillis;

        public StateTransitionGroup(State fromState, List<State> toStates, long expirationTimeMillis) {
            this.fromState = fromState;
            this.toStates = toStates;
            this.lastAccessTime = System.currentTimeMillis();
            this.expirationTimeMillis = expirationTimeMillis;
        }

        public State getFromState() {
            return fromState;
        }

        public List<State> getToStates() {
            return toStates;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - lastAccessTime > expirationTimeMillis;
        }

        @Override
        public String toString() {
            return "From: " + fromState + ", To: " + toStates;
        }
    }

    public List<State> getStatesTo(long id, State stateFrom) {
        if (stateTransitionMap.isEmpty()) return Collections.emptyList();
        StateTransitionGroup group = stateTransitionMap.get(id);
        return group != null ? group.getToStates() : null;
    }

    public void addWorkflow(Workflow workflow) {
        buildStateTransitionGroup(workflow);
    }

    private void buildStateTransitionGroup(Workflow workflow) {
        Map<State, List<State>> stateListMap = workflow.getStateTransitions().stream()
                .collect(Collectors.groupingBy(
                        StateTransition::getFromState,
                        Collectors.mapping(StateTransition::getToState, Collectors.toList())));
        for (Map.Entry<State, List<State>> entry : stateListMap.entrySet()) {
            StateTransitionGroup stateTransitionGroup = new StateTransitionGroup(
                    entry.getKey(),
                    entry.getValue(),
                    expirationTimeMillis
            );
            stateTransitionMap.put(workflow.getId(), stateTransitionGroup);
        }
    }
}
