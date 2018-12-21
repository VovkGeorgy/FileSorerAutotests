package by.home.fileSorterAutotest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class realise method whom read file by input path
 */
@Slf4j
@Service
public class FileGetter {

    /**
     * Method read file from input path
     *
     * @param folderPath folder input path
     * @return all files in input folder
     */
    public List<File> getFiles(String folderPath) {
        log.info("Try to read file");
        File folder = new File(folderPath);
        try {
            log.debug("Try to read file from path {}", folderPath);
            return Arrays.asList(folder.listFiles());
        } catch (NullPointerException e) {
            log.error("Cant read file from path {}, get exception []", folderPath, e.getMessage());
            return new ArrayList<>();
        }
    }
}