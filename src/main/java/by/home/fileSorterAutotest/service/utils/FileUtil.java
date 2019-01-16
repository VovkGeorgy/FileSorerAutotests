package by.home.fileSorterAutotest.service.utils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class consist methods to working with files
 */
public class FileUtil {

    /**
     * Get list of file names from file list
     *
     * @param filesList list of files
     * @return list of files names
     */
    public static List<String> getFilesNames(List<File> filesList) {
        return filesList.stream().map(File::getName).collect(Collectors.toList());
    }
}
