package fr.mowltnow.katatondeuse.presentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tondeuse")
public class TondeuseController {

	private static final Logger logger = LoggerFactory.getLogger(TondeuseController.class);

	@Value("${tondeuse.instructions.file}")
	private String filePath;

	@Value("${tondeuse.output.file}")
	private String outputFilePath;

	private final JobLauncher jobLauncher;
	private final Job processTondeuseInstructionsJob;

	public TondeuseController(JobLauncher jobLauncher, Job processTondeuseInstructionsJob) {
		this.jobLauncher = jobLauncher;
		this.processTondeuseInstructionsJob = processTondeuseInstructionsJob;
	}

	@GetMapping("/run")
	public String runJob(@RequestParam(value = "file", required = false) String file) {
		if (file == null || file.isEmpty()) {
			file = filePath;
		}
		checkAndPrepareOutputFile();
		try {
			// Ajoutez un horodatage pour garantir l'unicité des paramètres
			String uniqueJobParameters = file + "_" + System.currentTimeMillis();

			JobParameters jobParameters = new JobParametersBuilder().addString("input.file.name", file)
					.addString("ID", uniqueJobParameters).toJobParameters();

			jobLauncher.run(processTondeuseInstructionsJob, jobParameters);

			logger.info("Job successfully started with file: {}", file);
			return getResults();
		} catch (Exception e) {
			logger.error("Failed to start job", e);
			return "Failed to start job: " + e.getMessage();
		}
	}

	public String getResults() {
		try {
			Path path = Paths.get(outputFilePath);
			if (Files.exists(path)) {
				StringBuilder content = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path)))) {
					String line;
					while ((line = reader.readLine()) != null) {
						content.append(line).append("\n");
					}
				}
				return content.toString();
			} else {
				return "Output file not found.";
			}
		} catch (IOException e) {
			logger.error("Error reading output file", e);
			return "Error reading output file: " + e.getMessage();
		}
	}

	private void checkAndPrepareOutputFile() {
		try {
			Path path = Paths.get(outputFilePath);
			if (Files.exists(path)) {
				Files.write(path, new byte[0]); // Vide le fichier si il existe déjà
				logger.info("Cleared the existing output file: {}", outputFilePath);
			} else {
				Files.createFile(path); // Crée le fichier s'il n'existe pas
				logger.info("Created new output file: {}", outputFilePath);
			}
		} catch (IOException e) {
			logger.error("Error checking or preparing output file", e);
			throw new RuntimeException("Failed to prepare output file", e);
		}
	}
}