package org.example.awss3service.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

@Configuration
public class CloudWatchConfig {

	@Value("${aws.region}")
	private String region;

	@Value("${aws.access-key}")
	private String accessKey;

	@Value("${aws.secret-key}")
	private String secretKey;

	@Value("${aws.endpoint}")
	private String endpoint;

	@Bean
	public CloudWatchLogsClient cloudWatchLogsClient() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		return CloudWatchLogsClient.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.endpointOverride(URI.create(endpoint))
				.build();
	}
}
