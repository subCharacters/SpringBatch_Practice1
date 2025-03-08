package com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.template;

import com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.RetryableException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryTemplateItemProcessor implements ItemProcessor<String, String> {

    @Autowired
    private RetryTemplate retryTemplate;

    private int cnt = 0;

    @Override
    public String process(String item) throws Exception {
        Classifier<Throwable, Boolean> rollbackClassifier = new BinaryExceptionClassifier(true);

        String result = retryTemplate.execute(
                new RetryCallback() {
                    // 아이템 별로 retry 횟수만큼 실행
                    // chunk의 처음으로 돌아가지 않고 process에서 반복해서 재실행.
                    @Override
                    public String doWithRetry(RetryContext retryContext) throws Throwable {
                        if (item.equals("1") || item.equals("2")) {
                            cnt++;
                            throw new RetryableException("failed cnt : " + cnt);
                        }

                        return item;
                    }
                },
                // skip policy가 없다면 recover에서 데이터를 처리하여 정상처리시킴.
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext retryContext) throws Exception {
                        return item;
                    }
                }
                // , new DefaultRetryState(item, rollbackClassifier) // 이걸 설정하면 chunk의 처음부터 다시 시작.
        );
        return result;
    }
}
