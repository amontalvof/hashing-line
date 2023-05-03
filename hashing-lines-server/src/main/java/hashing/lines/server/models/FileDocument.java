package hashing.lines.server.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Document(value = "files")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public class FileDocument {
    @Id
    private String id;
    private String fileName;
    private String fileLocation;
    private String fileType;
    private long size;
    private LocalDateTime createdAt;
}
