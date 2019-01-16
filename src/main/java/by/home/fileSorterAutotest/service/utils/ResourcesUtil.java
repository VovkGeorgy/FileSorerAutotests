package by.home.fileSorterAutotest.service.utils;

import java.io.File;

/**
 * Class consist methods to working with resources
 */
public class ResourcesUtil {

    /**
     * Get full path to resources from relative path
     *
     * @param relativePath relative path to resources
     * @return full path to resources
     */
    public static String getResourcesPath(String relativePath) {
        return new File(ResourcesUtil.class.getResource(relativePath).getFile()).getPath() + "\\";
    }
}
