package kernel360.trackyconsumer.consumer.presentation;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel360.trackyconsumer.consumer.application.dto.request.CarOnOffRequest;
import kernel360.trackyconsumer.consumer.application.dto.request.GpsHistoryMessage;
import kernel360.trackyconsumer.consumer.application.service.ConsumerService;
import kernel360.trackycore.core.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/consume")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ConsumerController {

	private final ConsumerService ConsumerService;
	private final RabbitListenerEndpointRegistry registry;

	@PostMapping("/start")
	public ApiResponse<String> startConsumer() {
		log.info("메시지 수신 시작");
		registry.start();
		return ApiResponse.success("Consumer started");
	}

	@PostMapping("/stop")
	public ApiResponse<String> stopConsumer() {
		log.info("메시지 수신 중지");
		registry.stop();
		return ApiResponse.success("Consumer stopped");
	}

	@PostMapping("/on")
	public ApiResponse<String> postCarOn(@RequestBody CarOnOffRequest request) {

		ConsumerService.processOnMessage(request);
		return ApiResponse.success(request.mdn());
	}

	@PostMapping("/off")
	public ApiResponse<String> postCarOff(@RequestBody CarOnOffRequest request) {

		ConsumerService.processOffMessage(request);
		return ApiResponse.success(request.mdn());
	}

	@PostMapping("/cycle")
	public ApiResponse<String> postCarCycle(@RequestBody GpsHistoryMessage request) {

		ConsumerService.receiveCycleInfo(request);
		return ApiResponse.success(request.mdn());
	}
}
