package com.hq.heroes.common.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class UpdateAnnualLeaveSchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 자정마다 업데이트
//    @Scheduled(cron = "0/10 * * * * *", zone = "Asia/Seoul") // 테스트용 10초마다 업데이트
    public void runUpdateAnnualLeaveJob() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String date = dateFormat.format(new Date());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", date)
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("updateAnnualLeaveJob"), jobParameters);
    }
}
