package by.home.fileSorterAutotest.service.report.impl;


import by.home.fileSorterAutotest.entity.ExceptionMessage;
import by.home.fileSorterAutotest.service.report.IReportParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * CSV format file parser
 */
@Slf4j
@Service
public class CsvParser implements IReportParser<ExceptionMessage> {

    /**
     * Method parse input csv file
     *
     * @param file file to parse
     * @return entity from parsed file, or if can't parse file, return entity only with file name and false validity
     */
    @Override
    public ExceptionMessage parseFile(File file) {
        String filename = file.getName();
        log.info("Try to parse csv file {}", filename);
        try (Reader in = new FileReader(file)) {
            Iterable<CSVRecord> records =
                    CSVFormat.RFC4180.withHeader("messageType", "id", "message", "typeOfException", "throwingTime").parse(in);
            CSVRecord record = records.iterator().next();
            log.info("Csv file {} is parse successfully", filename);
            return validExceptionMessageBuilder(record, filename);
        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            log.error("Can't parse file {}, get exception \n {}", filename, e.getMessage());
            return new ExceptionMessage(filename, false);
        }
    }

    private ExceptionMessage validExceptionMessageBuilder(CSVRecord record, String filename) {
        return new ExceptionMessage(
                record.get("typeOfException"),
                record.get("messageType"),
                Long.parseLong(record.get("id")),
                record.get("message"),
                record.get("throwingTime"),
                filename,
                true
        );
    }
}