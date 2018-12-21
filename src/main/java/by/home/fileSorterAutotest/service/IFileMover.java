package by.home.fileSorterAutotest.service;

import java.io.File;
import java.util.List;

/**
 * Interface declare methods to moving some files
 */
public interface IFileMover {

    /**
     * Move files from path to path
     *
     * @param fileList list of files
     * @param fromFolder from path
     * @param toFolder to path
     */
    void move(List<File> fileList, String fromFolder, String toFolder);
}
