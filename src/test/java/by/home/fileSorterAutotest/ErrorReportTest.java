package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.LocalFileManager;
import by.home.fileSorterAutotest.service.SftpFileManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;

/**
 * Class test sorter of files
 */
@Slf4j
public class ErrorReportTest {

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

    @Parameters({"errorStorageFolder"})
    @AfterMethod
    public void tempCleaning(String cleaningDirectory) {
        localFileManager.cleanDirectory(cleaningDirectory, true);
    }

    @DataProvider
    public Object[][] errorReportTest() {
        log.info("Starting data provider");
        return new Object[][]{
                {"/testReports/error/valid/", "/errorFiles/valid/"},
                {"/testReports/error/notValid/", "/errorFiles/notValid/"},
        };
    }

    /**
     * Test sorter with error files
     *
     * @param fromFolder   folder from which copied files
     * @param remoteFolder sftp folder were sorter put files
     */
    @Test(dataProvider = "errorReportTest", timeOut = 5000)
    public void jsonFilesSorterTest(String fromFolder, String remoteFolder) {
        List<File> testFileList = localFileManager.getFiles(fromFolder, true);
        localFileManager.copy(testFileList, sorterInputFolder);
        localFileManager.waitFilesTransfer(sorterInputFolder);
        sftpFileManager.downloadFilesFromSftp(testFileList, remoteFolder, errorStorageFolder);
        List<File> fromSftpFiles = localFileManager.getFiles(errorStorageFolder, true);
        Assert.assertFalse(fromSftpFiles.isEmpty(), "There is no files on sftp");
        Assert.assertNotEquals(testFileList, fromSftpFiles, "Files received from sftp are not equals");
    }
}
