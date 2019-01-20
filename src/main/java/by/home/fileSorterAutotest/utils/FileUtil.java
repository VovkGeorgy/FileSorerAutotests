package by.home.fileSorterAutotest.utils;

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

    /**
     * Get full path to resources from relative path
     *
     * @param relativePath relative path to resources
     * @return full path to resources
     */
    public static String getResourcesPath(String relativePath) {
        return new File(FileUtil.class.getResource(relativePath).getFile()).getPath() + "\\";
    }
}
