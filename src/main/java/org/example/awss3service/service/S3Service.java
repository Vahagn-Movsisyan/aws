package org.example.awss3service.service;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

	private final S3Client s3Client;
	private final CloudWatchLoggerService cloudWatchLoggerService;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	public String uploadFile(MultipartFile file) throws IOException {
		String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
		cloudWatchLoggerService.log(String.format("Uploading file with: %s key", key));

		s3Client.putObject(PutObjectRequest.builder()
						.bucket(bucketName)
						.key(key)
						.contentType(file.getContentType())
						.build(),
				RequestBody.fromBytes(file.getBytes()));

		cloudWatchLoggerService.log(String.format("Uploaded file with: %s key", key));

		return getFileUrl(key);
	}

	public byte[] downloadFile(String key) {
		cloudWatchLoggerService.log(String.format("Downloading file with: %s", key));
		return s3Client.getObjectAsBytes(GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build()).asByteArray();
	}

	public String getFileUrl(String key) {
		return String.format("http://localhost:4566/%s/%s", bucketName, key);
	}
}
