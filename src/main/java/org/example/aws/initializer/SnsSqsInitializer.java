package org.example.aws.initializer;

import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.aws.service.MetadataHolderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;

@Component
@RequiredArgsConstructor
public class SnsSqsInitializer {

	private static final String SQS = "sqs";
	private static final String SQS_POLICY_JSON_PATCH = "/json/sqs-policy.json";

	@Value("${aws.sns.topic-name}")
	private String topicName;

	@Value("${aws.sqs.queue-name}")
	private String queueName;

	private final SnsClient snsClient;
	private final SqsClient sqsClient;
	private final MetadataHolderService metadataHolderService;

	@PostConstruct
	public void init() throws IOException {
		String topicArn = snsClient.createTopic(CreateTopicRequest.builder().name(topicName).build()).topicArn();
		metadataHolderService.setTopicArn(topicArn);

		String queueUrl = sqsClient.createQueue(CreateQueueRequest.builder().queueName(queueName).build()).queueUrl();
		String queueArn = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
				.queueUrl(queueUrl)
				.attributeNames(QueueAttributeName.QUEUE_ARN)
				.build())
				.attributes()
				.get(QueueAttributeName.QUEUE_ARN);

		String policy = loadPolicyFromJson(SQS_POLICY_JSON_PATCH, queueArn, topicArn);

		sqsClient.setQueueAttributes(SetQueueAttributesRequest.builder()
				.queueUrl(queueUrl)
				.attributes(Map.of(QueueAttributeName.POLICY, policy))
				.build());

		snsClient.subscribe(SubscribeRequest.builder()
				.topicArn(topicArn)
				.protocol(SQS)
				.endpoint(queueArn)
				.returnSubscriptionArn(true)
				.build());
	}

	private String loadPolicyFromJson(String resourcePath, String queueArn, String topicArn) throws IOException {
		InputStream is = getClass().getResourceAsStream(resourcePath);
		if (is == null) {
			throw new FileNotFoundException("Policy file not found: " + resourcePath);
		}
		String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		return json
				.replace("${RESOURCE_ARN}", queueArn)
				.replace("${SOURCE_ARN}", topicArn);
	}
}
