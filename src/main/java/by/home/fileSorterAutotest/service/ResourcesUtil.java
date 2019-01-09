package by.home.fileSorterAutotest.service;

import java.io.File;

/**
 * Class consist methods to working with resources
 */
class ResourcesUtil {

    /**
     * Get full path to resources from relative path
     *
     * @param relativePath relative path to resources
     * @return full path to resources
     */
    String getResourcesPath(String relativePath) {
        String asdasda = new File(this.getClass().getResource(relativePath).getFile()).getPath() + "\\";
        return asdasda;
    }
}
