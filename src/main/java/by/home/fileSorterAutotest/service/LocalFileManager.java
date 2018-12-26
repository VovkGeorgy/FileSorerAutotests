package by.home.fileSorterAutotest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class hold methods with primitive operations on local files
 */
@Slf4j
public class LocalFileManager {

    /**
     * Move files from path to path
     *
     * @param fileList list of files
     * @param toFolder to path
     */
    public void move(List<File> fileList, String toFolder) {
        for (File file : fileList) {
            String toPath = toFolder + file.getName();
            try {
                log.debug("Try to move file {}, to {}", file.getPath(), toPath);
                Files.move(Paths.get(file.getPath()), Paths.get(toPath));
            } catch (IOException e) {
                log.error("Get exception with moving files from {}, to {}", file.getPath(), toPath);
            }
        }
    }

    /**
     * Method get files from input path
     *
     * @param folderPath folder input path
     * @return all files in input folder
     */
    public List<File> getFiles(String folderPath) {
        File folder = new File(folderPath);
        try {
            log.debug("Try to get files from path {}", folderPath);
            return Arrays.asList(folder.listFiles());
        } catch (NullPointerException e) {
            log.error("Cant get files from path {}, get exception {}", folderPath, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Method get files from resources
     *
     * @param folderPath - path to resources files
     * @return list of files
     */
    public List<File> getResources(String folderPath) {
        try {
            File folder = ResourceUtils.getFile("classpath:" + folderPath);
            return Arrays.asList(folder.listFiles());
        } catch (NullPointerException | FileNotFoundException e) {
            log.error("Cant get files from path {}, get exception {}", folderPath, e.getMessage());
            return new ArrayList<>();
        }
    }
}
