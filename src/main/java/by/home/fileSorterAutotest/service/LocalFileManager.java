package by.home.fileSorterAutotest.service;

import by.home.fileSorterAutotest.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class hold methods with primitive operations on local files
 */
@Slf4j
@Service
public class LocalFileManager {

    /**
     * Copy files in list to target path
     *
     * @param fileList         list of files
     * @param targetFolderPath path to output folder
     */
    public void copyFiles(List<File> fileList, String targetFolderPath) {
        log.debug("Copy {} files to folder {}", fileList.size(), targetFolderPath);
        fileList.forEach(file -> {
            try {
                log.info("Try to copy file \n {}, to \n{}", file.getPath(), targetFolderPath);
                FileUtils.copyFileToDirectory(file, new File(targetFolderPath), false);
            } catch (IOException e) {
                log.error("Get exception with moving files from \n{}, to \n{}, exception \n{}", file.getPath(),
                        targetFolderPath, e.getMessage());
            }
        });
    }

    /**
     * Method get files from target path
     *
     * @param targetFolderPath folder target path
     * @param isResources      parameter show than files are resources
     * @return all files in target folder in list
     */
    public List<File> getFiles(String targetFolderPath, boolean isResources) {
        try {
            String folderPath = isResources ? FileUtil.getResourcesPath(targetFolderPath) :
                    targetFolderPath;
            log.info("Try to get files from path \n{}", targetFolderPath);
            return (List<File>) FileUtils.listFiles(new File(folderPath), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        } catch (NullPointerException nul) {
            log.error("Cant get files from path \n{}, get exception \n {}", targetFolderPath, nul.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Method wait when files are moved from folder or appear
     *
     * @param folderPath        target folder
     * @param maxWaitingTime    max times for scanning folder
     * @param folderMustBeEmpty folder must be empty or not
     * @return result of waiting folder condition, depends on @param folderMustBeEmpty
     */
    public boolean waitFilesTransfer(String folderPath, int maxWaitingTime, boolean folderMustBeEmpty) {
        try {
            log.info("Wait files transfer in folder {}", folderPath);
            double maxMethodTime = System.nanoTime() + maxWaitingTime * Math.pow(10, 9);
            boolean folderIsEmpty;
            do {
                File[] listFiles = new File(folderPath).listFiles();
                Thread.sleep(500);
                folderIsEmpty = (listFiles != null ? listFiles.length : 0) == 0;
            } while (!(folderIsEmpty == folderMustBeEmpty) && System.nanoTime() < maxMethodTime);
            return folderIsEmpty;
        } catch (InterruptedException | NullPointerException e) {
            log.error("Get exception \n {} with waiting file transfer in folder \n{}", e.getMessage(), folderPath);
            return false;
        }
    }

    /**
     * Method clean target directories
     *
     * @param directoryPaths paths of cleaning directories
     * @param isResources    show than directory is in resources
     */
    public void cleanDirectories(boolean isResources, String... directoryPaths) {
        for (String directoryPath : directoryPaths) {
            try {
                log.info("Clean directory {}", directoryPath);
                String folderPath = (isResources) ? FileUtil.getResourcesPath(directoryPath) :
                        directoryPath;
                FileUtils.cleanDirectory(new File(folderPath));
            } catch (IOException e) {
                log.error("Cant clean directory {}, get exception \n {}", directoryPath, e.getMessage());
            }
        }
    }
}
