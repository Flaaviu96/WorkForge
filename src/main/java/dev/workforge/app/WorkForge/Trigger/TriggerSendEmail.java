package dev.workforge.app.WorkForge.Trigger;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class TriggerSendEmail extends AbstractTrigger {

    @Override
    public Object fire() {
        return null;
    }
}
