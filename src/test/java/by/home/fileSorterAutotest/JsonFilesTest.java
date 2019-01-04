package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.LocalFileManager;
import by.home.fileSorterAutotest.service.SftpFileManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Class test sorter of files
 */
@Slf4j
public class JsonFilesTest {

    private LocalFileManager localFileManager;
    private SftpFileManager sftpFileManager;
    private String sorterInputFolder;
    private String errorStorageFolder;

    @Parameters({"sorterInputFolder", "errorStorageFolder", "ftpUsername", "ftpPassword", "ftpHost",
            "ftpPort", "ftpHostKeyChecking", "ftpHostKeyCheckingValue", "ftpChanelType"})
    @BeforeClass
    public void setUp(String sorterInputFolder, String errorStorageFolder, String ftpUsername, String ftpPassword, String ftpHost,
                      String ftpPort, String ftpHostKeyChecking, String ftpHostKeyCheckingValue, String ftpChanelType) {
        this.localFileManager = new LocalFileManager();
        this.sftpFileManager = new SftpFileManager(ftpUsername, ftpPassword, ftpHost, ftpPort,
                ftpHostKeyChecking, ftpHostKeyCheckingValue, ftpChanelType);
        this.sorterInputFolder = sorterInputFolder;
        this.errorStorageFolder = errorStorageFolder;
    }

    @DataProvider
    public Object[][] jsonSorterTest() {
        log.info("Starting data provider");
        return new Object[][]{
                {"/testFiles/json/valid/", "/errorFiles/valid/"},
                {"/testFiles/json/notValid/", "/errorFiles/notValid/"},
        };
    }

    /**
     * Test sorter with json files
     *
     * @param fromFolder   folder from which copied files
     * @param remoteFolder sftp folder were sorter put files
     */
    @Test(dataProvider = "jsonSorterTest", timeOut = 8000)
    public void jsonFilesSorterTest(String fromFolder, String remoteFolder) {
        List<File> testFileList = localFileManager.getFiles(fromFolder, true);
        localFileManager.move(testFileList, sorterInputFolder);
        localFileManager.waitFilesTransfer(testFileList, sorterInputFolder);
        sftpFileManager.moveFilesFromSftp(testFileList, remoteFolder, errorStorageFolder);
        List<File> fromSftpFiles = localFileManager.getFiles(errorStorageFolder, true);
        Assert.assertFalse(fromSftpFiles.isEmpty(), "There is no files on sftp");
        Assert.assertNotEquals(testFileList, fromSftpFiles, "Files received from sftp are not equals");
    }
}
