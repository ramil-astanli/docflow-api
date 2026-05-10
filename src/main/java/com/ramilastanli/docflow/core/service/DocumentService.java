package com.ramilastanli.docflow.core.service;

import com.ramilastanli.docflow.integration.DocumentGateway;
import com.ramilastanli.docflow.core.dto.request.DocumentRequestDto;
import com.ramilastanli.docflow.core.entity.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentGateway documentGateway;

    public Document createDocument(DocumentRequestDto requestDto, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            Path uploadDir = Paths.get("uploads");

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            requestDto.setFilePath(filePath.toString());
        }
        return documentGateway.startWorkFlow(requestDto);
    }
}
