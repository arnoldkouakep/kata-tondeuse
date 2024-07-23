package fr.mowltnow.katatondeuse.infrastructure;

import java.io.BufferedWriter;
import java.io.FileWriter;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import fr.mowltnow.katatondeuse.domain.Tondeuse;
import fr.mowltnow.katatondeuse.domain.Tondeuse.Instruction;
import fr.mowltnow.katatondeuse.domain.TondeuseService;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	private static final Logger logger = LoggerFactory.getLogger(BatchConfig.class);

	private final ResourceLoader resourceLoader;
	private final TondeuseService tondeuseService;

	public BatchConfig(ResourceLoader resourceLoader, TondeuseService tondeuseService) {
		this.resourceLoader = resourceLoader;
		this.tondeuseService = tondeuseService;
	}

	@Bean
	public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			InstructionReader reader, @Qualifier("instructionProcessor") ItemProcessor<Instruction, Tondeuse> processor,
			@Qualifier("instructionWriter") ItemWriter<? super Tondeuse> writer) {
		return new StepBuilder("step", jobRepository).<Instruction, Tondeuse>chunk(1, transactionManager).reader(reader)
				.processor(processor).writer(writer).build();
	}

	@Bean
	public Job processTondeuseInstructionsJob(JobRepository jobRepository, @Qualifier("step") Step step) {
		return new JobBuilder("processInstructionsJob", jobRepository).preventRestart().start(step)
				.incrementer(new RunIdIncrementer()) // Permet aux jobs avec les mêmes paramètres d'être redémarrés
				.build();
	}

	@Bean
	public ItemProcessor<Instruction, Tondeuse> instructionProcessor() {
		return new ItemProcessor<Instruction, Tondeuse>() {

			@Override
			public Tondeuse process(Instruction instruction) throws Exception {
				return tondeuseService.executeInstructions(instruction);
			}
		};
	}

	@Bean
	public InstructionReader instructionReader(@Value("${tondeuse.instructions.file}") String filePath) {
		return new InstructionReader(resourceLoader, filePath);
	}

	@Bean
	public ItemWriter<Tondeuse> instructionWriter(@Value("${tondeuse.output.file}") String outputFilePath) {
		return new ItemWriter<Tondeuse>() {

			@Override
			public void write(Chunk<? extends Tondeuse> items) throws Exception {

				// Convertir le chemin du fichier en chemin absolu
				java.nio.file.Path filePath = java.nio.file.Paths.get(outputFilePath).toAbsolutePath();

				// Créer le répertoire si nécessaire
				java.nio.file.Path parentDir = filePath.getParent();
				if (parentDir != null && !java.nio.file.Files.exists(parentDir)) {
					java.nio.file.Files.createDirectories(parentDir);
				}

				// Créer le fichier s'il n'existe pas
				if (!java.nio.file.Files.exists(filePath)) {
					java.nio.file.Files.createFile(filePath);
				}
				logger.info("Destination path file : {}", filePath.toString());

				try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
					StringBuilder output = new StringBuilder();

					for (Tondeuse item : items) {
						logger.info("Tondeuse {}", item.toString());
						output.append(item.toString() + " ");
						writer.write(output.toString());
					}
				}
			}
		};
	}

	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder.setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
				.addScript("classpath:org/springframework/batch/core/schema-h2.sql").build();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager getTransactionManager() {
		return new ResourcelessTransactionManager();
	}

	@Bean(name = "jobRepository")
	public JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource());
		factory.setTransactionManager(getTransactionManager());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean(name = "jobLauncher")
	public JobLauncher getJobLauncher() throws Exception {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}
}