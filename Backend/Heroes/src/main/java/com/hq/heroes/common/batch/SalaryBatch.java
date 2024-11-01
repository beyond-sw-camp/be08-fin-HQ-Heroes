package com.hq.heroes.common.batch;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.service.NotificationService;
import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.entity.SalaryHistory;
import com.hq.heroes.salary.repository.SalaryHistoryRepository;
import com.hq.heroes.salary.service.SalaryHistoryService;
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
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SalaryBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EmployeeRepository employeeRepository;
    private final SalaryHistoryService salaryHistoryService;
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final NotificationService notificationService;

    @Bean
    public Job salaryJob() {
        return new JobBuilder("salaryJob", jobRepository)
                .start(salaryStep())
                .build();
    }

    // 읽기 -> 처리 -> 쓰기
    @Bean
    public Step salaryStep() {
        return new StepBuilder("salaryStep", jobRepository)
                .<Employee, SalaryHistory>chunk(10, platformTransactionManager)
                .reader(salaryReader())
                .processor(salaryProcessor())
                .writer(salaryWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Employee> salaryReader() {
        return new RepositoryItemReaderBuilder<Employee>()
                .name("EmployeeReader")
                .pageSize(10)
                .methodName("findEmployeesWithAttendance")
                .repository(employeeRepository)
                .sorts(Map.of("employeeId", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Employee, SalaryHistory> salaryProcessor() {
        return employee -> {

            SalaryHistoryDTO requestDTO = SalaryHistoryDTO.builder()
                    .employeeId(employee.getEmployeeId())
                    .build();

            SalaryHistoryDTO result = salaryHistoryService.createSalary(requestDTO);

            notificationService.sendNotificationAsync(result.getEmployeeId(), AutoNotificationType.MONTHLY_SALARY_PAYMENT, result);

            return SalaryHistory.builder()
                    .employee(employee)
                    .preTaxTotal(result.getPreTaxTotal())
                    .postTaxTotal(result.getPostTaxTotal())
                    .salaryMonth(result.getSalaryMonth())
                    .employmentInsurance(result.getEmploymentInsurance())
                    .healthInsurance(result.getHealthInsurance())
                    .incomeTax(result.getIncomeTax())
                    .localIncomeTax(result.getLocalIncomeTax())
                    .nationalPension(result.getNationalPension())
                    .longTermCare(result.getLongTermCare())
                    .bonus(result.getBonus())
                    .workTime(result.getWorkTime())
                    .overTime(result.getOverTime())
                    .build();
        };
    }

    @Bean
    public RepositoryItemWriter<SalaryHistory> salaryWriter() {
        return new RepositoryItemWriterBuilder<SalaryHistory>()
                .repository(salaryHistoryRepository)
                .methodName("save")
                .build();
    }
}
