package by.home.fileSorterAutotest.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Class realise method which move JSON file from local folder to sftp server
 */
@Slf4j
@Service
public class FileMover implements IFileMover {

    @Override
    public void move(List<File> fileList, String fromFolder, String toFolder) {
        log.info("Try to moving files");
        for (File file : fileList) {
            String fromPath = fromFolder + file.getName();
            String toPath = toFolder + file.getName();
            try {
                log.debug("Try to move file {}, to {}", fromPath, toPath);
                Files.move(Paths.get(fromPath), Paths.get(toPath));
            } catch (IOException e) {
                log.error("Get exception with moving files from {}, to {}", fromPath, toPath);
            }
        }
    }
}