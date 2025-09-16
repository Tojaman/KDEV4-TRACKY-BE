package kernel360.trackyconsumer.common.config;

import java.util.concurrent.Executor;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConfig {

	private final RabbitMQProperties properties;
	private final MeterRegistry meterRegistry;

	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	// RabbitMQ 리스너 전용 쓰레드 풀 생성
	@Bean(name = "rabbitListenerExecutor")
    public Executor rabbitListenerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 코어와 최대 쓰레드 수를 7로 고정
        executor.setCorePoolSize(7);
        executor.setMaxPoolSize(7);
        executor.setThreadNamePrefix("RabbitListener-"); // 쓰레드 이름 접두사 설정
        executor.initialize();
        log.info("RabbitMQ 리스너 전용 쓰레드 풀을 생성했습니다. (size=7)");
        return executor;
    }

	@Bean
	public SimpleRabbitListenerContainerFactory batchRabbitListenerContainerFactory(
		ConnectionFactory connectionFactory,
		@Qualifier("rabbitListenerExecutor") Executor rabbitListenerExecutor) {

		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setBatchSize(properties.getBatch().getSize());
		factory.setReceiveTimeout(properties.getBatch().getTimeout());
		factory.setBatchListener(properties.getBatch().isEnabled());
		factory.setConsumerBatchEnabled(properties.getBatch().isConsumerBatchEnabled());
		factory.setMessageConverter(messageConverter());

		// 리스너 컨테이너 스레드(I/O 쓰레드)
		factory.setConcurrentConsumers(7);
		factory.setMaxConcurrentConsumers(7);

		// 직접 만든 전용 쓰레드 풀을 사용하도록 설정(처리 쓰레드)
        factory.setTaskExecutor(rabbitListenerExecutor);

		// 메트릭 측정을 위한 설정
		factory.setMicrometerEnabled(true);
		factory.setObservationEnabled(true);

		// 오류 핸들링 설정
		factory.setErrorHandler(throwable -> {
			log.error("RabbitMQ 메시지 처리 중 오류 발생: {}", throwable.getMessage(), throwable);
		});

		factory.setDefaultRequeueRejected(properties.getBatch().isDefaultRequeueRejected());

		return factory;
	}
}
