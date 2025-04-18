package org.example.aws.migration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.aws.service.CloudWatchLoggerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Component
@RequiredArgsConstructor
public class DynamoDbMigrationRunner {

	private static final String FILE_ID = "fileId";

	@Value("${aws.dynamoDB.table-name}")
	private String tableName;

	private final DynamoDbClient dynamoDbClient;
	private final CloudWatchLoggerService cloudWatchLoggerService;

	@PostConstruct
	public void init() {
		if (!isTableExists()) {
			cloudWatchLoggerService.log(String.format("Creating DynamoDB table %s", tableName));
			createTable();
		}
		cloudWatchLoggerService.log(String.format("DynamoDB table %s is already exist", tableName));
	}

	private void createTable() {
		CreateTableRequest createTableRequest = CreateTableRequest.builder()
				.tableName(tableName)
				.attributeDefinitions(getAttributeDefinition())
				.keySchema(getKeySchemaElement())
				.billingMode(BillingMode.PAY_PER_REQUEST)
				.build();
		dynamoDbClient.createTable(createTableRequest);
	}

	private KeySchemaElement getKeySchemaElement() {
		return KeySchemaElement.builder()
				.attributeName(FILE_ID)
				.keyType(KeyType.HASH)
				.build();
	}

	private AttributeDefinition getAttributeDefinition() {
		return AttributeDefinition.builder()
				.attributeName(FILE_ID)
				.attributeType(ScalarAttributeType.S)
				.build();
	}

	private boolean isTableExists() {
		try {
			dynamoDbClient.describeTable(DescribeTableRequest.builder()
					.tableName(tableName)
					.build());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
