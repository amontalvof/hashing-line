package hashing.lines.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import hashing.lines.server.models.FileDocument;

public interface HashingLinesRepository extends MongoRepository<FileDocument, String> {
    public FileDocument findByFileName(String fileName);
}
