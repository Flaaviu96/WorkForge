package dev.workforge.app.WorkForge.Model;

import dev.workforge.app.WorkForge.Trigger.AbstractTrigger;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
public class StateTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transition_seq")
    @SequenceGenerator(name = "transition_seq", sequenceName = "transition_id_seq", allocationSize = 50)
    private long id;

    @ManyToOne
    @JoinColumn(name = "from_state_id")
    private State fromState;

    @ManyToOne
    @JoinColumn(name = "to_state_id")
    private State toState;

    @ManyToOne
    @JoinColumn(name = "Workflow_id")
    private Workflow workflow;

    @ManyToOne
    @JoinColumn(name = "trigger_id")
    private AbstractTrigger trigger;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateTransition that = (StateTransition) o;
        return id == that.id && Objects.equals(fromState, that.fromState) && Objects.equals(toState, that.toState);
    }

    public State getFromState() {
        return fromState;
    }

    public State getToState() {
        return toState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromState, toState);
    }
}
