package kernel360.trackybatch.job;

import java.time.LocalDate;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import kernel360.trackybatch.statistic.application.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StatisticJobConfig {

    private final StatisticService statisticService;

    @Bean
    public Job statisticJob(JobRepository jobRepository, Step dailyStatisticStep, Step monthlyStatisticStep) {
        return new JobBuilder("statisticJob", jobRepository)
                .start(dailyStatisticStep)
                .next(monthlyStatisticStep)
                .build();
    }

    @Bean
    @JobScope
    public Step dailyStatisticStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                   Tasklet dailyStatisticTasklet) { // Tasklet Bean을 주입받음
        return new StepBuilder("dailyStatisticStep", jobRepository)
                .tasklet(dailyStatisticTasklet, transactionManager) // 주입받은 Tasklet을 사용
                .build();
    }

    @Bean
    @JobScope
    public Step monthlyStatisticStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                     Tasklet monthlyStatisticTasklet) { // Tasklet Bean을 주입받음
        return new StepBuilder("monthlyStatisticStep", jobRepository)
                .tasklet(monthlyStatisticTasklet, transactionManager) // 주입받은 Tasklet을 사용
                .build();
    }

    @Bean
    @StepScope // Step 실행 시점에 Bean이 생성되도록 설정
    public Tasklet dailyStatisticTasklet(@Value("#{jobParameters['targetDate']}") String targetDateStr) { // jobParameters에서 값을 주입받음
        return (contribution, chunkContext) -> {
            LocalDate targetDate = LocalDate.parse(targetDateStr);
            log.info("[Daily Statistic] Target Date: {}", targetDate);
            statisticService.dailyStatistic(targetDate);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope // Step 실행 시점에 Bean이 생성되도록 설정
    public Tasklet monthlyStatisticTasklet(@Value("#{jobParameters['targetDate']}") String targetDateStr) { // jobParameters에서 값을 주입받음
        return (contribution, chunkContext) -> {
            LocalDate targetDate = LocalDate.parse(targetDateStr);
            log.info("[Monthly Statistic] Target Date: {}", targetDate);
            statisticService.monthlyStatistic(targetDate);
            return RepeatStatus.FINISHED;
        };
    }
}
