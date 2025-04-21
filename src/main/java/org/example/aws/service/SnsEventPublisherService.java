package org.example.aws.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@RequiredArgsConstructor
public class SnsEventPublisherService {

	private final SnsClient snsClient;
	private final MetadataHolderService metadataHolderService;

	public void publish(String message) {
		String topicArn = metadataHolderService.getTopicArn();

		PublishRequest publishRequest = PublishRequest.builder()
				.topicArn(topicArn)
				.message(message).build();

		snsClient.publish(publishRequest);
	}
}
