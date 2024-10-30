package com.hq.heroes.common.batch;

import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.repository.EvaluationRepository;
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
                .methodName("findAll")
                .repository(evaluationRepository)
                .sorts(Map.of("evaluationId", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Evaluation, Notification> evaluationToNotificationProcessor() {

        return new ItemProcessor<Evaluation, Notification>() {
            @Override
            public Notification process(Evaluation item) throws Exception {

                System.out.println(" item.isNotificationSent() = " +  item.isNotificationSent());
                // 이게 true로 나와야 함 그래야 if문 진입
                
                /*
                    테스트용 @ @
                 */
                if (!item.isNotificationSent()) {
                    System.out.println("=============== true ================= ");
                    AutoNotificationType notificationType = AutoNotificationType.EVALUATION_RESULT;

                    Map<String, Object> params = new HashMap<>();
                    params.put("receiverId", item.getEmployee().getEmployeeId());

                    item.setNotificationSent(true);
                    evaluationRepository.save(item);
                    return notificationService.sendAutomaticNotification(notificationType, params, item);
                }

                /*
                    평가 등록 3일 후 알림 발송
                 */

//                LocalDateTime createdAt = item.getCreatedAt();
//                LocalDateTime threeDaysLater = createdAt.plusDays(3); // 생성일로부터 3일 후
//
//                if (!item.isNotificationSent() && LocalDateTime.now().isAfter(threeDaysLater)) {
//                    AutoNotificationType  notificationType = AutoNotificationType.EVALUATION_RESULT;
//                    Map<String, Object> params = new HashMap<>();
//                    params.put("receiverId", item.getEvaluationId());
//
//                    item.setNotificationSent(true);
//                    evaluationRepository.save(item);
//                    notificationService.sendAutomaticNotification(notificationType, params, item);
//                }

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
