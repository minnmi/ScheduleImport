package com.example.read.files.service;

import com.example.read.files.infrastructure.CSVHelper;
import com.example.read.files.infrastructure.ExcelHelper;
import com.example.read.files.model.Tutorial;
import com.example.read.files.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ParseService {

    @Autowired
    TutorialRepository tutorialRepository;

    public void saveExcelFile(MultipartFile file) {
        try {
            var workBook = ExcelHelper.getWorkbook(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()));
            List<Tutorial> tutorials = ExcelHelper.excelToTutorials(workBook);
            tutorialRepository.saveAll(tutorials);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public void saveCsvFile(MultipartFile file) {
        try {
            List<Tutorial> tutorials = CSVHelper.csvToTutorials(file);
            tutorialRepository.saveAll(tutorials);
        } catch (Exception e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public List<Tutorial> getAllTutorialsByExcel() {
        return tutorialRepository.findAll();
    }

    public List<Tutorial> getAllTutorialsByCsv() {
        return tutorialRepository.findAll();
    }
}
