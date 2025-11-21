package kernel360trackybe.trackyhub.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

	private final RabbitMQProperties properties;
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void init(ApplicationReadyEvent event) {
		// 이벤트에서 ApplicationContext를 통해 RabbitAdmin을 가져와 초기화
		RabbitAdmin admin = event.getApplicationContext().getBean(RabbitAdmin.class);
		log.info("RabbitMQ 구조 강제 초기화 실행 (RabbitAdmin.initialize)");
		admin.initialize();
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(properties.getExchange().getCarInfo());
	}

	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(properties.getExchange().getCycleInfo());
	}

	@Bean
	public TopicExchange deadLetterExchange() {
		return new TopicExchange(properties.getExchange().getDlx());
	}

	@Bean
	public Queue gpsQueue() {
		log.info("Queue 생성: gpsQueue");
		return QueueBuilder.durable(properties.getQueue().getGps())
			.withArgument("x-dead-letter-exchange", properties.getExchange().getDlx())
			.withArgument("x-dead-letter-routing-key", properties.getRouting().getDeadLetterKey())
			.build();
	}

	@Bean
	public Queue onOffQueue() {
		return new Queue(properties.getQueue().getOnoff(), true);
	}

	@Bean
	public Queue deadLetterQueue() {
		return new Queue(properties.getQueue().getDlq());
	}

	@Bean
	public Queue webQueue() {
		return new Queue(properties.getQueue().getWeb(), true);
	}

	@Bean
	public Binding gpsBinding(Queue gpsQueue, FanoutExchange exchange) {
		return BindingBuilder.bind(gpsQueue).to(exchange);
	}

	@Bean
	public Binding webBinding(Queue webQueue, FanoutExchange exchange) {
		return BindingBuilder.bind(webQueue).to(exchange);
	}

	@Bean
	public Binding onBinding(Queue onOffQueue, TopicExchange exchange) {
		return BindingBuilder.bind(onOffQueue).to(exchange).with(properties.getRouting().getOnKey());
	}

	@Bean
	public Binding offBinding(Queue onOffQueue, TopicExchange exchange) {
		return BindingBuilder.bind(onOffQueue).to(exchange).with(properties.getRouting().getOffKey());
	}

	@Bean
	public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
		return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(properties.getRouting().getDeadLetterKey());
	}

	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	// ConnectionFactory는 Spring Boot에서 자동으로 생성해주므로 주입받아 사용한다.
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter());
		return rabbitTemplate;
	}
}
