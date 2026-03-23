package com.porlio.porliobe.module.shared.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  /**
   * Custom thread pool cho async tasks.
   * <p>
   * Tại sao không dùng default SimpleAsyncTaskExecutor? → Default tạo thread mới cho MỖI task →
   * không có giới hạn → Nếu 1000 user đăng ký cùng lúc → 1000 threads → OOM
   * <p>
   * ThreadPoolTaskExecutor cho phép kiểm soát số threads.
   */
  @Bean
  public TaskExecutor notificationTaskExecutor() {
    // You can customize the thread pool settings here
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // Số thread luôn sẵn sàng
    executor.setCorePoolSize(2);

    // Số thread tối đa có thể tạo ra
    executor.setMaxPoolSize(5);

    // Queue chờ trước khi tạo thêm thread
    executor.setQueueCapacity(100);

    // Tên thread để dễ dàng theo dõi trong logs
    executor.setThreadNamePrefix("NotificationExecutor-");

    // Khi shutdown, chờ cho các task hoàn thành thay vì ngắt quãng
    executor.setWaitForTasksToCompleteOnShutdown(true);
    // Thời gian chờ tối đa để các task hoàn thành khi shutdown (tính bằng giây)
    executor.setAwaitTerminationSeconds(30);

    // Khởi tạo executor
    executor.initialize();
    return executor;
  }
}
