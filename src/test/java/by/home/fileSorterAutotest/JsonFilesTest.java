package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.LocalFileManager;
import by.home.fileSorterAutotest.service.SftpFileManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class test sorter of files
 */
@Slf4j
public class JsonFilesTest {

    private LocalFileManager localFileManager;
    private SftpFileManager sftpFileManager;
    private String notSortedFolderPath;
    private String fromSftpStorage;
    private Map<String, String> ftpConfigMap = new HashMap<>();

    @Parameters({"notSortedFolderPath", "fromSftpStorage", "ftpUsername", "ftpPassword", "ftpHost", "ftpPort", "ftpHostKeyChecking", "ftpHostKeyCheckingValue", "ftpChanelType"})
    @BeforeClass
    public void setUp(String notSortedFolderPath, String fromSftpStorage, String ftpUsername, String ftpPassword, String ftpHost,
                      String ftpPort, String ftpHostKeyChecking, String ftpHostKeyCheckingValue, String ftpChanelType) {
        ftpConfigMap.put("ftpUsername", ftpUsername);
        ftpConfigMap.put("ftpPassword", ftpPassword);
        ftpConfigMap.put("ftpHost", ftpHost);
        ftpConfigMap.put("ftpPort", ftpPort);
        ftpConfigMap.put("ftpHostKeyChecking", ftpHostKeyChecking);
        ftpConfigMap.put("ftpHostKeyCheckingValue", ftpHostKeyCheckingValue);
        ftpConfigMap.put("ftpChanelType", ftpChanelType);
        this.localFileManager = new LocalFileManager();
        this.sftpFileManager = new SftpFileManager();
        this.notSortedFolderPath = notSortedFolderPath;
        this.fromSftpStorage = fromSftpStorage;
    }

    @DataProvider
    public Object[][] jsonSorterTest() {
        log.info("Starting data provider");
        return new Object[][]{
                {"/testFiles/json/valid/", "/jsonFiles/valid/"},
                {"/testFiles/json/notValid/", "/jsonFiles/notValid/"},
        };
    }

    /**
     * Test sorter with json files
     *
     * @param fromFolder   folder from which copied files
     * @param remoteFolder sftp folder were sorter put files
     */
    @Test(dataProvider = "jsonSorterTest")
    public void jsonFilesSorterTest(String fromFolder, String remoteFolder) throws InterruptedException {
        List<File> testFileList = localFileManager.getResources(fromFolder);
        localFileManager.move(testFileList, notSortedFolderPath);
        while (isFilesExist(testFileList)) {
            log.debug("Wait when sorter move files from {}", notSortedFolderPath);
        }
        sftpFileManager.getFilesFromSftp(testFileList, remoteFolder, fromSftpStorage, ftpConfigMap);
        List<File> getedFromSftpFiles = localFileManager.getResources(fromSftpStorage);
        Assert.assertFalse(getedFromSftpFiles.isEmpty(), "There is no files on sftp");
        Assert.assertNotEquals(testFileList, getedFromSftpFiles, "Files gated from sftp are not equals");
    }

    private boolean isFilesExist(List<File> testFileList) {
        boolean filesExist = false;
        for (File file : testFileList) filesExist = new File(notSortedFolderPath + file.getName()).exists() || filesExist;
        return filesExist;
    }
}
