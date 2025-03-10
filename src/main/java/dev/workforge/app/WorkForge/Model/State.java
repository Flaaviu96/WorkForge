package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_seq")
    @SequenceGenerator(name = "state_seq", sequenceName = "state_id_seq", allocationSize = 50)
    private long id;

    private String name;

    private StateType stateType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id == state.id && Objects.equals(name, state.name) && stateType == state.stateType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, stateType);
    }
}
