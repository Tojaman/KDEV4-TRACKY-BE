package kernel360.trackyconsumer.consumer.application.service;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import kernel360.trackyconsumer.consumer.application.dto.request.CarOnOffRequest;
import kernel360.trackyconsumer.consumer.application.dto.request.GpsHistoryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageListener {
	private final ConsumerService consumerService;

	@RabbitListener(queues = "on-off-Queue")
	public void receiveCarOnOffMessage(@Payload CarOnOffRequest message,
		@Header("amqp_receivedRoutingKey") String routingKey) {
		try {
			switch (routingKey) {
				case "onKey":
					consumerService.processOnMessage(message);
					break;
				case "offKey":
					consumerService.processOffMessage(message);
					break;
			}
		} catch (Exception e) {
			log.error("Error processing message: {}", e.getMessage());
		}
	}

	// GPS 정보 처리 큐
	@RabbitListener(queues = "gps-queue", containerFactory = "batchRabbitListenerContainerFactory")
	public void receiveCarMessages(List<GpsHistoryMessage> messages) {
		log.info("GPS 메시지 배치 수신: {} 개", messages.size());

		for (GpsHistoryMessage message : messages) {
			try {
				log.info("GPS 메시지 수신: {}", message.toString());
				consumerService.receiveCycleInfo(message);
			} catch (Exception e) {
				log.error("GPS 메시지 처리 중 오류 발생: {}", e.getMessage());
			}
		}
	}
}
