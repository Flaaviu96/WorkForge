package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.StateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    @Query(
            "SELECT st FROM State st " +
            "WHERE st.stateType = :stateType"
    )
    State findStateByStateType(@Param("stateType") StateType stateType);
}
