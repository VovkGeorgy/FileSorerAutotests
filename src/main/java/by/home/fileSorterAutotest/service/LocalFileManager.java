package by.home.fileSorterAutotest.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class hold methods with primitive operations on local files
 */
@Slf4j
public class LocalFileManager {

    /**
     * Move files in list to target path
     *
     * @param fileList list of files
     * @param toFolder to path
     */
    public void move(List<File> fileList, String toFolder) {
        File targetDirectory = new File(toFolder);
        fileList.forEach(file -> {
            try {
                log.debug("Try to move file {}, to {}", file.getPath(), toFolder);
                FileUtils.moveFileToDirectory(file, targetDirectory, false);
            } catch (IOException e) {
                log.error("Get exception with moving files from {}, to {}, exception {}", file.getPath(), toFolder, e.getMessage());
            }
        });
    }

    /**
     * Method get files from target path
     *
     * @param targetFolderPath folder target path
     * @param isResources      parameter show than files are resources
     * @return all files in target folder
     */
    public List<File> getFiles(String targetFolderPath, boolean isResources) {
        log.debug("Try to get files from path {}", targetFolderPath);
        String folderPath = (isResources) ? new File(this.getClass().getResource(targetFolderPath).getFile()).getPath() : targetFolderPath;
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            return paths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Cant get files from path {}, get exception \n {}", folderPath, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Method wait when files are moved from folder
     *
     * @param fileList   files
     * @param folderPath target folder
     */
    public void waitFilesTransfer(List<File> fileList, String folderPath) {
        while (isFilesExist(fileList, folderPath)) {
            log.debug("Wait when sorter move files from {}", fileList);
        }
    }

    private boolean isFilesExist(List<File> testFileList, String folderPath) {
        boolean filesExist = false;
        for (File file : testFileList) filesExist = new File(folderPath + file.getName()).exists() || filesExist;
        return filesExist;
    }
}
