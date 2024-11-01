package com.hq.heroes.common.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class SalarySchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    // 매월 10일 자정 실행 (Asia/Seoul 시간 기준)
    @Scheduled(cron = "0 0 0 10 * *", zone = "Asia/Seoul")
//    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Seoul")
    public void runSalaryJob() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = dateFormat.format(new Date());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", date)
                .toJobParameters();

        // salaryJob 실행
        jobLauncher.run(jobRegistry.getJob("salaryJob"), jobParameters);
    }
}
