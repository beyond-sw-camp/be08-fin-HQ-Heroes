package com.hq.heroes.common.batch;

import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.repository.EvaluationRepository;
import com.hq.heroes.notification.dto.NotificationResDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.repository.NotificationRepository;
import com.hq.heroes.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SendEvaluationBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EvaluationRepository evaluationRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Bean
    public Job sendEvaluationBatchJob() {

        return new JobBuilder("sendEvaluationBatchJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 매번 다른 ID로 Job 실행
                .start(sendEvaluationBatchStep())
                .build();
    }

    @Bean
    public Step sendEvaluationBatchStep() {
        return new StepBuilder("sendEvaluationBatchStep", jobRepository)
                .<Evaluation, Notification>chunk(10, platformTransactionManager)
                .reader(evaluationReader())
                .processor(evaluationToNotificationProcessor())
                .writer(notificationWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Evaluation> evaluationReader() {
        return new RepositoryItemReaderBuilder<Evaluation>()
                .name("evaluationReader")
                .pageSize(10)
                .methodName("findAllWithEmployeeAndEvaluator")
                .repository(evaluationRepository)
                .sorts(Map.of("evaluationId", Sort.Direction.ASC))
                .build();
    }


    @Bean
    public ItemProcessor<Evaluation, Notification> evaluationToNotificationProcessor() {

        return new ItemProcessor<Evaluation, Notification>() {
            @Override
            public Notification process(Evaluation item) throws Exception {

                /*
                    테스트용 @ @
                 */

                if (!item.isNotificationSent()) {
                    AutoNotificationType notificationType = AutoNotificationType.EVALUATION_RESULT;

                    Map<String, Object> params = new HashMap<>();
                    params.put("receiverId", item.getEmployee().getEmployeeId());

                    item.setNotificationSent(true);
                    evaluationRepository.save(item);

                    return notificationService.sendAutomaticNotification(notificationType, params, item);
                }



                /*
                    실제 로직 - > 마감일 6/30, 12/30일에 알림발송
                 */

//                LocalDate today = LocalDate.now();
//                int month = today.getMonthValue();
//                int day = today.getDayOfMonth();
//
//                // 오늘이 6월 30일 또는 12월 30일인 경우에만 수행
//                if ((month == 6 && day == 30) || (month == 12 && day == 30)) {
//                    if (!item.isNotificationSent()) {
//                        System.out.println("=============== true ================= ");
//                        AutoNotificationType notificationType = AutoNotificationType.EVALUATION_RESULT;
//
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("receiverId", item.getEmployee().getEmployeeId());
//
//                        item.setNotificationSent(true);
//                        evaluationRepository.save(item);
//                        return notificationService.sendAutomaticNotification(notificationType, params, item);
//                    }
//                }
//

                return null;
            }
        };
    }

    @Bean
    public RepositoryItemWriter<Notification> notificationWriter() {
        return new RepositoryItemWriterBuilder<Notification>()
                .repository(notificationRepository)
                .methodName("save")
                .build();
    }

}
