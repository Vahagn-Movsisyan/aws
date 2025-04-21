package org.example.aws.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AWSServicesConfig {

	@Value("${aws.region}")
	private String region;

	@Value("${aws.access-key}")
	private String accessKey;

	@Value("${aws.secret-key}")
	private String secretKey;

	@Value("${aws.endpoint}")
	private String endpoint;


	@Bean
	public S3Client s3Client() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		return S3Client.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.endpointOverride(URI.create(endpoint))
				.forcePathStyle(true)
				.build();
	}

	@Bean
	public CloudWatchLogsClient cloudWatchLogsClient() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		return CloudWatchLogsClient.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.endpointOverride(URI.create(endpoint))
				.build();
	}

	@Bean
	public DynamoDbClient dynamoDbClient() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		return DynamoDbClient.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.endpointOverride(URI.create(endpoint))
				.build();
	}

	@Bean
	public SnsClient snsClient() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		return SnsClient.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.endpointOverride(URI.create(endpoint))
				.build();
	}

	@Bean
	public SqsClient sqsClient() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		return SqsClient.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.endpointOverride(URI.create(endpoint))
				.build();
	}
}
