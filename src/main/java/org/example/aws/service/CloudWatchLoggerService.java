package org.example.aws.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CreateLogGroupRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.CreateLogStreamRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsResponse;

@Service
@RequiredArgsConstructor
public class CloudWatchLoggerService {

	@Value("${aws.cloudWatch.log.group.name}")
	private String logGroupName;

	@Value("${aws.cloudWatch.log.stream.name}")
	private String logStreamName;

	private String sequenceToken;

	private final CloudWatchLogsClient cloudWatchLogsClient;

	@PostConstruct
	public void init() {
		try {
			cloudWatchLogsClient.createLogGroup(CreateLogGroupRequest.builder().logGroupName(logGroupName).build());
		} catch (Exception e) {

		}
		try {
			cloudWatchLogsClient.createLogStream(CreateLogStreamRequest.builder().logStreamName(logStreamName).build());
		} catch (Exception e) {

		}
	}

	public void log(String message) {
		InputLogEvent inputLogEvent = InputLogEvent.builder()
				.message(message)
				.timestamp(System.currentTimeMillis())
				.build();

		PutLogEventsRequest.Builder putLogEventsRequest = PutLogEventsRequest.builder()
				.logGroupName(logGroupName)
				.logStreamName(logStreamName)
				.logEvents(inputLogEvent);

		if (sequenceToken != null) {
			putLogEventsRequest.sequenceToken(sequenceToken);
		}

		PutLogEventsResponse putLogEventsResponse = cloudWatchLogsClient.putLogEvents(putLogEventsRequest.build());
		sequenceToken = putLogEventsResponse.nextSequenceToken();
	}
}
