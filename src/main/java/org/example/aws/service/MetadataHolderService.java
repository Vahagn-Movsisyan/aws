package org.example.aws.service;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class MetadataHolderService {
	private String topicArn;
}
