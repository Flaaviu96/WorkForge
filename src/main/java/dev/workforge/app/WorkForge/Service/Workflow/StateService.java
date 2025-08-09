package dev.workforge.app.WorkForge.Service.Workflow;

import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.StateType;

public interface StateService {

    State loadStateByStateType(StateType stateType);
}
