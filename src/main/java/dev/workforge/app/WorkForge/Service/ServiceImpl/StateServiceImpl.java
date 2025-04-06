package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.StateType;
import dev.workforge.app.WorkForge.Repository.StateRepository;
import dev.workforge.app.WorkForge.Service.StateService;
import org.springframework.stereotype.Service;

@Service
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    public StateServiceImpl(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public State loadStateByStateType(StateType stateType) {
        return stateRepository.findStateByStateType(stateType);
    }
}
