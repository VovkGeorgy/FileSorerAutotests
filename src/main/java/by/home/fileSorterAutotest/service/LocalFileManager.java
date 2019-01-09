package by.home.fileSorterAutotest.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * @param fileList      list of files
     * @param outFolderPath path to output folder
     */
    public void copy(List<File> fileList, String outFolderPath) {
        log.debug("Copy {} files to folder {}", fileList.size(), outFolderPath);
        File outFolder = new File(outFolderPath);
        fileList.forEach(file -> {
            try {
                log.info("Try to copy file {}, to {}", file.getPath(), outFolderPath);
                FileUtils.copyFileToDirectory(file, outFolder, false);
            } catch (IOException e) {
                log.error("Get exception with moving files from {}, to {}, exception {}", file.getPath(), outFolderPath, e
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
            log.info("Try to get files from path {}", targetFolderPath);
            File targetFolder = new File(folderPath);
            return (List<File>) FileUtils.listFiles(targetFolder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        } catch (NullPointerException nul) {
            log.error("Cant get files from path {}, get exception \n {}", targetFolderPath, nul.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Method wait when files are moved from folder
     *
     * @param folderPath target folder
     */
    public void waitFilesTransfer(String folderPath) {
        log.info("Wait when sorter copy files from {}", folderPath);
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(folderPath))) {
            if (dirStream.iterator().hasNext()) waitFilesTransfer(folderPath);
        } catch (IOException e) {
            log.error("Get exception \n {} with waiting file transfer in folder {}", e.getMessage(), folderPath);
        }
    }

    /**
     * Method chen target directory
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
