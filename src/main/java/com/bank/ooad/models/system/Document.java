package com.bank.ooad.models.system;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String documentId;
    
    private String type;
    private String filePath;
    private LocalDateTime uploadedAt;
    private String ocrStatus;

    public void upload() {
        System.out.println("Uploading document: " + filePath);
        this.uploadedAt = LocalDateTime.now();
    }

    public void requestReupload() {
        System.out.println("Requesting document reupload");
    }

    // Getters/Setters
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getOcrStatus() { return ocrStatus; }
    public void setOcrStatus(String ocrStatus) { this.ocrStatus = ocrStatus; }
}
