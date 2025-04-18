package org.example.aws.controller;

import lombok.RequiredArgsConstructor;
import org.example.aws.model.FileMetadata;
import org.example.aws.service.FileMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file-metadata")
public class FileMetadataController {

	private final FileMetadataService fileMetadataService;

	@GetMapping("/{fileId}")
	public ResponseEntity<FileMetadata> getFileMetadata(@PathVariable String fileId) {
		return ResponseEntity.ok(fileMetadataService.getFileMetadata(fileId));
	}
}
