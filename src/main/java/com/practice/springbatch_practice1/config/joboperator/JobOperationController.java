package com.practice.springbatch_practice1.config.joboperator;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import java.util.Set;

@RestController
public class JobOperationController {

    private final JobOperator jobOperator;
    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;

    public JobOperationController(JobOperator jobOperator, JobRepository jobRepository, JobExplorer jobExplorer) {
        this.jobOperator = jobOperator;
        this.jobRepository = jobRepository;
        this.jobExplorer = jobExplorer;
    }

    // job을 시작한다.
    // 아래와 같은 로직은 이미 실행 이력이 있는 잡을 찾아서 실행한다.ㅁ
    @GetMapping("/batch/start")
    public String start(@RequestParam String id) throws Exception {
        boolean isExist = false;
        for (String s : jobRepository.getJobNames()) {
            SimpleJob job = new SimpleJob(s);
            System.out.println("job name : " + job.getName());

            // 스프링 배치5부터는 프로퍼티를 명시하는 것으로 바뀌었다.
            // 스프링 배치4에서는 문자열로 넘겼으나 가독성을 위해서 바뀐 것 같다.
            Properties jobParameters = new Properties();
            jobParameters.setProperty("id", id);

            isExist = true;
            jobOperator.start(job.getName(), jobParameters);
        }

        // 실행 이력이 없는 잡을 실행할때에는 아래와 같이 하거나.
        // 잡 런처로 직접 실행 시키는 것도 방법일듯하다.
        if (!isExist) {
            jobOperator.start("jobOperationJob", new Properties());

        }

        return "batch is started";
    }


    // job을 정지한다.
    // 현재 실행 중인 step까지는 실행이 되는것 같다.
    // 청크라면 읽기 중이면 읽기까지, 쓰기면 쓰기까지만 하고 중단하는 것 같다.
    @GetMapping("/batch/stop")
    public String stop() throws Exception {
        for (String s : jobRepository.getJobNames()) {
            SimpleJob job = new SimpleJob(s);
            System.out.println("job name : " + job.getName());

            Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(job.getName());

            JobExecution jobExecution = runningJobExecutions.iterator().next();

            jobOperator.stop(jobExecution.getId());
        }

        return "job is stopped";
    }

    // job을 재시작한다.
    // 실패한 job을 재시작하는 것이므로 정상종료된 job은 재시작 하지 못한다.
    @GetMapping("/batch/restart")
    public String restart() throws Exception {
        for (String s : jobRepository.getJobNames()) {
            SimpleJob job = new SimpleJob(s);
            System.out.println("job name : " + job.getName());

            JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
            JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
            jobOperator.restart(lastJobExecution.getJobId());
        }
        return "job is restarted";
    }
}
