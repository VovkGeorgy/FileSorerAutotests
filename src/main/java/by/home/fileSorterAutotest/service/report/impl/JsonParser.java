package by.home.fileSorterAutotest.service.report.impl;

import by.home.fileSorterAutotest.entity.ErrorMessage;
import by.home.fileSorterAutotest.service.report.IReportParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * JSON format file parser
 */
@Slf4j
@Service
public class JsonParser implements IReportParser<ErrorMessage> {

    /**
     * Method parse input json file
     *
     * @param file input file
     * @return entity from parsed file or if cant't parse - return entity only with file name and false validity
     */
    @Override
    public ErrorMessage parseFile(File file) {
        String filename = file.getName();
        log.info("Try to parse csv file {}", filename);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ErrorMessage errorMessage = objectMapper.readValue(file, new TypeReference<ErrorMessage>() {
            });
            errorMessage.setFileName(filename);
            errorMessage.setValid(true);
            log.info("Json file {} is parse successfully", filename);
            return errorMessage;
        } catch (IOException e) {
            log.error("Cant parse file {}, IOException \n", filename, e.getMessage());
            return notValidErrorMessageBuilder(filename);
        }
    }

    private ErrorMessage notValidErrorMessageBuilder(String filename) {
        ErrorMessage notValidErrorMessage = new ErrorMessage();
        notValidErrorMessage.setFileName(filename);
        notValidErrorMessage.setValid(false);
        return notValidErrorMessage;
    }
}
