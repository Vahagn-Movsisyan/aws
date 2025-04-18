package org.example.aws.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadata {
	private String fileId;
	private String originalFileName;
	private String s3Key;
	private String contentType;
	private long size;
	private String uploadTime;
}
