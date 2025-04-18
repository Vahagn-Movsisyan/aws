package org.example.aws.controller;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.aws.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

	private final S3Service s3Service;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		return ResponseEntity.ok(s3Service.uploadFile(file));
	}

	@GetMapping("/download/{key}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable String key) {
		return ResponseEntity.ok(s3Service.downloadFile(key));
	}
}
