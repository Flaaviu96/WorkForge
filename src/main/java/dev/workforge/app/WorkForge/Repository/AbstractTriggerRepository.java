package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Trigger.AbstractTrigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbstractTriggerRepository extends JpaRepository<AbstractTrigger, Long> {
}
