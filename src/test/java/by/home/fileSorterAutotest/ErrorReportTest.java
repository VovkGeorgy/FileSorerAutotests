package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.LocalFileManager;
import by.home.fileSorterAutotest.service.SftpFileManager;
import by.home.fileSorterAutotest.service.utils.FileUtil;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;

/**
 * Class test sorter of files
 */
public class ErrorReportTest {

    private LocalFileManager localFileManager;
    private SftpFileManager sftpFileManager;
    private String sorterInputFolder;
    private String temporaryFiles;
    private int maxWaitingTime;

    @Parameters({"sorterInputFolder", "temporaryFiles", "ftpUsername", "ftpPassword", "ftpHost",
            "ftpPort", "ftpHostKeyChecking", "ftpHostKeyCheckingValue", "ftpChanelType", "maxWaitingTime"})
    @BeforeClass
    public void setUp(String sorterInputFolder, String temporaryFiles, String ftpUsername, String ftpPassword, String ftpHost,
                      String ftpPort, String ftpHostKeyChecking, String ftpHostKeyCheckingValue, String ftpChanelType, int
                              maxWaitingTime) {
        this.localFileManager = new LocalFileManager();
        this.sftpFileManager = new SftpFileManager(ftpUsername, ftpPassword, ftpHost, ftpPort,
                ftpHostKeyChecking, ftpHostKeyCheckingValue, ftpChanelType);
        this.sorterInputFolder = sorterInputFolder;
        this.temporaryFiles = temporaryFiles;
        this.maxWaitingTime = maxWaitingTime;
        localFileManager.cleanDirectory(sorterInputFolder, false);
    }

    @Parameters({"temporaryFiles"})
    @BeforeMethod
    @AfterMethod
    public void clean(String temporaryFiles) {
        localFileManager.cleanDirectory(temporaryFiles, true);
        sftpFileManager.cleanDirectory("/errorFiles/valid/");
        sftpFileManager.cleanDirectory("/errorFiles/notValid/");
    }

    @DataProvider
    public Object[][] errorReportTest() {
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
    @Test(dataProvider = "errorReportTest")
    public void jsonFilesSorterTest(String fromFolder, String remoteFolder) {
        List<File> errorReportsList = localFileManager.getFiles(fromFolder, true);
        localFileManager.copy(errorReportsList, sorterInputFolder);
        Assert.assertTrue(localFileManager.waitFilesTransfer(sorterInputFolder, maxWaitingTime, true),
                "Files are not moved from sorter input folder " + sorterInputFolder);
        sftpFileManager.downloadFilesFromSftp(remoteFolder, temporaryFiles, true);
        List<File> fromSftpFiles = localFileManager.getFiles(temporaryFiles, true);
        List<String> errorReportsFileNames = FileUtil.getFilesNames(errorReportsList);
        List<String> fromSftpFileNames = FileUtil.getFilesNames(fromSftpFiles);
        Assert.assertFalse(fromSftpFiles.isEmpty(), "List of files received from sftp is empty");
        Assert.assertEquals(errorReportsFileNames, fromSftpFileNames,
                "Names from files received from sftp and shipped to sorter are not equals");
    }
}
