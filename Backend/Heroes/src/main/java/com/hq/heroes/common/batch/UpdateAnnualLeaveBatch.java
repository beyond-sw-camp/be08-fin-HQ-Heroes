package com.hq.heroes.common.batch;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UpdateAnnualLeaveBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EmployeeRepository employeeRepository;

    @Bean
    public Job updateAnnualLeaveJob() {

        return new JobBuilder("updateAnnualLeaveJob", jobRepository)
                .start(updateAnnualLeaveStep())
                .build();
    }

    @Bean
    public Step updateAnnualLeaveStep() {
        return new StepBuilder("updateAnnualLeaveStep", jobRepository)
                .<Employee, Employee>chunk(10, platformTransactionManager)
                .reader(employeeReader())
                .processor(annualLeaveProcessor())
                .writer(employeeWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Employee> employeeReader() {

        return new RepositoryItemReaderBuilder<Employee>()
                .name("employeeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(employeeRepository)
                .sorts(Map.of("employeeId", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Employee, Employee> annualLeaveProcessor() {

        return new ItemProcessor<Employee, Employee>() {

            /*
                테스트용 @ @
             */
//            @Override
//            public Employee process(Employee item) throws Exception {
//                item.setAnnualLeave(item.getAnnualLeave() + 1);
//                return item; // 수정된 객체 반환
//            }

            /*
                입사일 한달 기준 업데이트
             */
            @Override
            public Employee process(Employee item) throws Exception {
                LocalDate JoinDate = item.getJoinDate(); // 입사일 가져오기
                LocalDate now = LocalDate.now();

                // 입사일로부터 한 달이 지났는지 확인
                if (JoinDate != null && ChronoUnit.MONTHS.between(JoinDate, now) >= 1) {
                    item.setAnnualLeave(item.getAnnualLeave() + 4);
                    return item; // 업데이트된 객체 반환
                } else {
                    return null; // 한 달이 지나지 않은 경우 null 반환
                }
            }

        };
    }

    @Bean
    public RepositoryItemWriter<Employee> employeeWriter() {
        return new RepositoryItemWriterBuilder<Employee>()
                .repository(employeeRepository)
                .methodName("save")
                .build();
    }
}
