package com.example.read.files.infrastructure;

import com.example.read.files.model.Tutorial;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class CSVHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };
    static final Character[] DELIMITERS = {';', ',', '\t'};
    static final char NO_DELIMITER = '\0'; //empty char

    static char detectDelimiter(InputStream file) throws IOException {
        try (
                final var reader = new BufferedReader(new InputStreamReader(file));
        ) {
            String line = reader.readLine();

            return Arrays.stream(DELIMITERS)
                    .filter(s -> line.contains(s.toString()))
                    .findFirst()
                    .orElse(NO_DELIMITER);
        }
    }

    static char detectDelimiter2(InputStream file) throws IOException {
        char delimiter = ',';
        try (
                final var reader = new BufferedReader(new InputStreamReader(file));
        ) {

            // Remove Strings do CSV
            List<String> linesWoStrings = reader.lines()
                    .map(l -> l.replaceAll("\"(.|\s)*?\"", ""))
                    .filter(Predicate.not(String::isBlank))
                    .toList();

            int maxMatches = -1;
            for (var d : DELIMITERS) {
                // Conta a quantidade de splits houve em cada linha para um dado delimiter
                var countSplitsForLines = linesWoStrings
                        .stream()
                        .map(l -> l.split(d.toString()).length)
                        .toList();

                // Se todos são iguais, então sabemos que todos devem ser igual ao primeiro
                var numMatches = countSplitsForLines.get(0);

                // O delimiter correto é o que possui a maior quantidade de splits e um delimiter possui a mesma
                // possui a mesma quantidade de splits em cada linha
                if (numMatches > maxMatches && countSplitsForLines.stream().allMatch(l -> l == numMatches)) {
                    maxMatches = numMatches;
                    delimiter = d;
                }
            }
        }
        return delimiter; // Retorna o melhor encontrado, senão retorna a virugla mesmo (ie, o default)
    }



    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }


    public static List<Tutorial> csvToTutorials(MultipartFile is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT
//                             .builder().setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setTrim(true)
                             .withFirstRecordAsHeader()
                             .withIgnoreHeaderCase()
                             .withTrim()
                             .withDelimiter(detectDelimiter2(is.getInputStream())));)
        {

            List<Tutorial> tutorials = new ArrayList<Tutorial>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Tutorial tutorial = new Tutorial(
                        Long.parseLong(csvRecord.get("Id")),
                        csvRecord.get("Title"),
                        csvRecord.get("Description"),
                        Boolean.parseBoolean(csvRecord.get("Published"))
                );

                tutorials.add(tutorial);
            }

            return tutorials;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
