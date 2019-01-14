package by.home.fileSorterAutotest.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class hold methods with primitive operations on local files
 */
@Slf4j
public class LocalFileManager {

    private ResourcesUtil resourcesUtil = new ResourcesUtil();

    /**
     * Copy files in list to target path
     *
     * @param fileList         list of files
     * @param targetFolderPath path to output folder
     */
    public void copy(List<File> fileList, String targetFolderPath) {
        log.debug("Copy {} files to folder {}", fileList.size(), targetFolderPath);
        fileList.forEach(file -> {
            try {
                log.info("Try to copy file \n {}, to \n{}", file.getPath(), targetFolderPath);
                FileUtils.copyFileToDirectory(file, new File(targetFolderPath), false);
            } catch (IOException e) {
                log.error("Get exception with moving files from \n{}, to \n{}, exception \n{}", file.getPath(),
                        targetFolderPath, e
                        .getMessage());
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
        try {
            String folderPath = isResources ? resourcesUtil.getResourcesPath(targetFolderPath) :
                    targetFolderPath;
            log.info("Try to get files from path \n{}", targetFolderPath);
            File targetFolder = new File(folderPath);
            return (List<File>) FileUtils.listFiles(targetFolder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
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
     * @return is the target folder empty
     */
    public boolean waitFilesTransfer(String folderPath, int maxWaitingTime, boolean folderMustBeEmpty) {
        try {
            log.info("Wait when folder {} will isEmpty - {}", folderPath, folderMustBeEmpty);
            File targetFolder = new File(folderPath);
            double maxMethodTime = System.nanoTime() + maxWaitingTime * Math.pow(10, 9);
            boolean folderIsEmpty;
            do {
                File[] listFiles = targetFolder.listFiles();
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
     * Method clean target directory
     *
     * @param directoryPath path of cleaning directory
     */
    public void cleanDirectory(String directoryPath, boolean isResources) {
        log.info("Clean directory {}", directoryPath);
        String folderPath = (isResources) ? new File(this.getClass().getResource(directoryPath).getFile()).getPath() :
                directoryPath;
        try {
            FileUtils.cleanDirectory(new File(folderPath));
        } catch (IOException e) {
            log.error("Cant clean directory {}, get exception \n {}", directoryPath, e.getMessage());
        }
    }
}
