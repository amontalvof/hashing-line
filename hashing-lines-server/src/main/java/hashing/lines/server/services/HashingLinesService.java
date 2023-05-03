package hashing.lines.server.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import hashing.lines.server.dto.CustomMultipartFile;
import hashing.lines.server.models.FileDocument;
import hashing.lines.server.repositories.HashingLinesRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashingLinesService {
    private final HashingLinesRepository hashingLinesRepository;

    public void uploadFile(MultipartFile multipartFile) {
        try {
            InputStream inputStream = multipartFile.getInputStream();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String lines = reader.lines().map(line -> this.hashLine(line)).collect(Collectors.joining("\n"));

            byte[] inputArray = lines.getBytes();
            CustomMultipartFile customMultipartFile = new CustomMultipartFile(inputArray);

            String fileName = multipartFile.getOriginalFilename();

            Path filePath = this.resolveFileLocation(fileName);

            FileDocument fileDocument = FileDocument.builder()
                    .fileName(fileName)
                    .fileLocation(filePath.toString())
                    .fileType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .createdAt(LocalDateTime.now())
                    .build();

            hashingLinesRepository.save(fileDocument);
            this.storeFile(customMultipartFile, multipartFile.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashLine(String line) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash = Base64.getEncoder().encodeToString(digest.digest(line.getBytes(StandardCharsets.UTF_8)));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    private Path resolveTargetLocation() {
        Path fileStorageLocation = Paths.get("src/main/java/hashing/lines/server/download")
                .toAbsolutePath().normalize();
        return fileStorageLocation;
    }

    private Path resolveFileLocation(String fileName) {
        Path fileStorageLocation = this.resolveTargetLocation();
        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        return filePath;
    }

    private void storeFile(MultipartFile file, String fileName) {
        try {
            String cleanFileName = StringUtils.cleanPath(fileName);
            Path fileStorageLocation = this.resolveTargetLocation();
            Files.createDirectories(fileStorageLocation);
            Path targetLocation = fileStorageLocation.resolve(cleanFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ResponseEntity<Resource> downloadFile(String fileName) {
        Resource resource = this.loadFileAsResource(fileName);
        String contentType = "application/octet-stream";
        ResponseEntity<Resource> response = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        resource.getFilename() + "\"")
                .body(resource);
        return response;
    }

    private Resource loadFileAsResource(String fileName) {
        try {
            FileDocument fileDocument = hashingLinesRepository.findByFileName(fileName);
            Path filePath = Paths.get(fileDocument.getFileLocation());
            Resource resource = new UrlResource(filePath.toUri());
            return resource;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
