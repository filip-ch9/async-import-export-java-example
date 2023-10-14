package app.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class AsyncConfig implements WebMvcConfigurer {

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

    configurer.setTaskExecutor(mvcTaskExecutor());
    configurer.setDefaultTimeout(30000);
  }

  @Bean
  public ThreadPoolTaskExecutor mvcTaskExecutor() {

    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(10);
    threadPoolTaskExecutor.setThreadNamePrefix("mvc-task-");
    return threadPoolTaskExecutor;
  }
}