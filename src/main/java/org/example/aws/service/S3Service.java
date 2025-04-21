package org.example.aws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.aws.model.FileMetadata;
import org.example.aws.utils.BuilderUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

	@Value("${aws.s3.bucket}")
	private String bucketName;

	private final S3Client s3Client;
	private final ObjectMapper objectMapper;
	private final CloudWatchLoggerService cloudWatchLoggerService;
	private final SnsEventPublisherService snsEventPublisherService;

	public String uploadFile(MultipartFile file) throws IOException {
		UUID uuid = UUID.randomUUID();

		String key = uuid + "_" + file.getOriginalFilename();
		cloudWatchLoggerService.log(String.format("Uploading file with: %s key", key));

		s3Client.putObject(PutObjectRequest.builder()
						.bucket(bucketName)
						.key(key)
						.contentType(file.getContentType())
						.build(),
				RequestBody.fromBytes(file.getBytes()));

		cloudWatchLoggerService.log(String.format("Uploaded file with: %s key", key));

		FileMetadata fileMetadata = BuilderUtils.buildFileMetadata(UUID.randomUUID().toString(), file.getOriginalFilename(), key, file.getContentType(), file.getSize(), Instant.now().toString());

		publishEvent(fileMetadata);
		cloudWatchLoggerService.log(String.format("Uploaded file with: %s key and size %s", key, file.getSize()));

		return getFileUrl(key);
	}

	public byte[] downloadFile(String key) {
		cloudWatchLoggerService.log(String.format("Downloading file with: %s", key));
		return s3Client.getObjectAsBytes(GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build()).asByteArray();
	}

	private void publishEvent(FileMetadata fileMetadata) throws JsonProcessingException {
		String fileMetadataJson = objectMapper.writeValueAsString(fileMetadata);
		cloudWatchLoggerService.log(String.format("Publishing file metadata: %s", fileMetadataJson));
		snsEventPublisherService.publish(fileMetadataJson);
	}

	private String getFileUrl(String key) {
		return String.format("http://localhost:4566/%s/%s", bucketName, key);
	}
}
