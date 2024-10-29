package com.hq.heroes.common.batch;

import com.hq.heroes.evaluation.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SendEvaluationBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EvaluationRepository evaluationRepository;


}
