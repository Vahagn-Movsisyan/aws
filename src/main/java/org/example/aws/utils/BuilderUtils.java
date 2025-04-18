package org.example.aws.utils;

import lombok.experimental.UtilityClass;
import org.example.aws.model.FileMetadata;

@UtilityClass
public class BuilderUtils {

	public static FileMetadata buildFileMetadata(String fileId, String fileName, String s3Key, String contentType, long size, String createdTime) {
		return FileMetadata.builder()
				.fileId(fileId)
				.originalFileName(fileName)
				.s3Key(s3Key)
				.contentType(contentType)
				.size(size)
				.uploadTime(createdTime)
				.build();
	}
}
