package fr.mowltnow.katatondeuse;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KataTondeuseApplication {

	public static void main(String[] args) {
		SpringApplication.run(KataTondeuseApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(JobLauncher jobLauncher, Job job,
			@Value("${tondeuse.instructions.file}") String filePath) {
		return args -> {
			JobParameters jobParameters = new JobParametersBuilder().addString("ID", filePath).toJobParameters();

			jobLauncher.run(job, jobParameters);
		};
	}
}
