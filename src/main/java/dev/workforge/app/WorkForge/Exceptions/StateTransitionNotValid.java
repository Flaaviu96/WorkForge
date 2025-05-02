package dev.workforge.app.WorkForge.Exceptions;

public class StateTransitionNotValid extends RuntimeException {
    public StateTransitionNotValid(String message) {
        super(message);
    }
}
