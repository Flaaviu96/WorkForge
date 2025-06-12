package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.StateTransition;
import dev.workforge.app.WorkForge.Model.Workflow;
import dev.workforge.app.WorkForge.Trigger.AbstractTrigger;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class WorkflowFactory {
    private final Map<Long, List<StateTransitionGroup>> stateTransitionMap = new ConcurrentHashMap<>();
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
        Iterator<Map.Entry<Long, List<StateTransitionGroup>>> entryIterator = stateTransitionMap.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Long, List<StateTransitionGroup>> entry = entryIterator.next();
            List<StateTransitionGroup> stateTransitionGroup = entry.getValue();

            for (StateTransitionGroup transitionGroup : stateTransitionGroup) {
                if (transitionGroup.isExpired()) {
                    entryIterator.remove();
                    break;
                }
            }
        }
    }

    private class StateTransitionGroup {
        private State fromState;
        private Map<State, AbstractTrigger> toStates;
        private long lastAccessTime;
        private final long expirationTimeMillis;

        public StateTransitionGroup(State fromState, Map<State, AbstractTrigger> toStates, long expirationTimeMillis) {
            this.fromState = fromState;
            this.toStates = toStates;
            this.lastAccessTime = System.currentTimeMillis();
            this.expirationTimeMillis = expirationTimeMillis;
        }

        public State getFromState() {
            return fromState;
        }

        public Map<State, AbstractTrigger> getToStates() {
            return new HashMap<>(toStates);
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

    public Map<State, AbstractTrigger> getStatesTo(long id, State stateFrom) {
        List<StateTransitionGroup> group = stateTransitionMap.get(id);
        if (group == null || group.isEmpty()) return Collections.emptyMap();

        return group.stream()
                .filter(stateTransitionGroup -> stateTransitionGroup.getFromState().getName().equals(stateFrom.getName()))
                .findFirst()
                .map(StateTransitionGroup::getToStates)
                .orElse(Collections.emptyMap());
    }

    public Map<State, List<State>> getWorkflowForSpecificProject(long projectId) {
        List<StateTransitionGroup> transitionGroups = stateTransitionMap.get(projectId);
        return transitionGroups.stream()
                .collect(Collectors.toMap(
                        StateTransitionGroup::getFromState,
                        group -> new ArrayList<>(group.getToStates().keySet())
                ));
    }

    public void addWorkflow(Workflow workflow) {
        buildStateTransitionGroup(workflow);
    }

    public State getStateToByName(long workflowId, String stateName) {
        List<StateTransitionGroup> groups = stateTransitionMap.get(workflowId);
        if (groups == null) {
            return null;
        }

        for (StateTransitionGroup group : groups) {
            for (Map.Entry<State, AbstractTrigger> entry : group.getToStates().entrySet()) {
                if (entry.getKey().getName().equals(stateName)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public AbstractTrigger getTrigger(long workflowId, String stateFrom, State stateTo) {
        List<StateTransitionGroup> groups = stateTransitionMap.get(workflowId);
        if (groups == null) {
            return null;
        }
        for (StateTransitionGroup group : groups) {
            if (group.getFromState().getName().equals(stateFrom)) {
                return group.toStates.get(stateTo);
            }
        }
        return null;
    }

    private void buildStateTransitionGroup(Workflow workflow) {
        Map<State, Map<State, AbstractTrigger>> stateListMap = workflow.getStateTransitions().stream()
                .collect(Collectors.groupingBy(
                        StateTransition::getFromState,
                        Collectors.toMap(
                                StateTransition::getToState,
                                StateTransition::getTrigger
                        )
                ));
        List<StateTransitionGroup> stateTransitionGroupList = new ArrayList<>();
        for (Map.Entry<State, Map<State, AbstractTrigger>> entry : stateListMap.entrySet()) {
            StateTransitionGroup stateTransitionGroup = new StateTransitionGroup(
                    entry.getKey(),
                    entry.getValue(),
                    expirationTimeMillis
            );
            stateTransitionGroupList.add(stateTransitionGroup);
            stateTransitionMap.put(workflow.getId(), stateTransitionGroupList);
        }
     }
}
