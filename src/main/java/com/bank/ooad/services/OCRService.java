package com.bank.ooad.services;

import com.bank.ooad.models.system.Document;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class OCRService {

    public boolean scan(Document doc) {
        System.out.println("Scanning document: " + doc.getDocumentId());
        return true;
    }

    public Map<String, String> extractData(Document doc) {
        System.out.println("Extracting data from document");
        Map<String, String> data = new HashMap<>();
        data.put("extracted", "true");
        return data;
    }
}
