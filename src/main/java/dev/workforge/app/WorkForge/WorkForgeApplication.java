package dev.workforge.app.WorkForge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class WorkForgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkForgeApplication.class, args);
	}

}
