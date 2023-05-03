package hashing.lines.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import hashing.lines.server.services.HashingLinesService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class HashingLinesController {

    private final HashingLinesService hashingLinesService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        hashingLinesService.uploadFile(multipartFile);
    }

    @GetMapping("/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {
        return hashingLinesService.downloadFile(fileName);
    }

}
