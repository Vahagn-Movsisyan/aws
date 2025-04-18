package org.example.aws.service;

import lombok.RequiredArgsConstructor;
import org.example.aws.dao.FileMetadataServiceDAO;
import org.example.aws.execption.NotFoundException;
import org.example.aws.model.FileMetadata;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileMetadataService {

	private final FileMetadataServiceDAO fileMetadataServiceDAO;
	private final CloudWatchLoggerService cloudWatchLoggerService;

	public FileMetadata getFileMetadata(String fileId) {
		return fileMetadataServiceDAO.getFileMetadata(fileId).orElseThrow(() -> {
			cloudWatchLoggerService.log("ERROR: File not found with file id: " + fileId);
			return new NotFoundException("File not found: " + fileId);
		});
	}
}
