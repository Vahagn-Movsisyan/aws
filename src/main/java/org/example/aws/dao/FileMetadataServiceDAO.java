package org.example.aws.dao;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.aws.model.FileMetadata;
import org.example.aws.service.CloudWatchLoggerService;
import org.example.aws.utils.BuilderUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@Service
@RequiredArgsConstructor
public class FileMetadataServiceDAO {

	public static final String FILE_ID = "fileId";
	public static final String ORIGINAL_FILE_NAME = "originalFileName";
	public static final String S3_KEY = "s3Key";
	public static final String CONTENT_TYPE = "contentType";
	public static final String SIZE = "size";
	public static final String UPLOAD_TIME = "uploadTime";

	@Value("${aws.dynamoDB.table-name}")
	private String tableName;

	private final DynamoDbClient dynamoDbClient;
	private final CloudWatchLoggerService cloudWatchLoggerService;

	public Optional<FileMetadata> getFileMetadata(String fileId) {
		Map<String, AttributeValue> key = Map.of(FILE_ID, AttributeValue.fromS(fileId));

		GetItemRequest getItemRequest = GetItemRequest.builder()
				.key(key)
				.tableName(tableName)
				.build();

		GetItemResponse response = dynamoDbClient.getItem(getItemRequest);
		Map<String, AttributeValue> item = response.item();

		try {
			return Optional.of(BuilderUtils.buildFileMetadata(
					item.get(FILE_ID).s(),
					item.get(ORIGINAL_FILE_NAME).s(),
					item.get(S3_KEY).s(),
					item.get(CONTENT_TYPE).s(),
					Long.parseLong(item.get(SIZE).n()),
					item.get(UPLOAD_TIME).s()
			));
		} catch (Exception e) {
			cloudWatchLoggerService.log(String.format("Error getting file metadata for fileId: %s details %s", fileId, e.getMessage()));
		}
		return Optional.empty();
	}
}
