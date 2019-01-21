package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.config.AppConfig;
import by.home.fileSorterAutotest.repository.ErrorRepository;
import by.home.fileSorterAutotest.service.LocalFileManager;
import by.home.fileSorterAutotest.service.SftpFileManager;
import by.home.fileSorterAutotest.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;

/**
 * Class test report sorter
 */
@Test
@ContextConfiguration(classes = AppConfig.class, loader = AnnotationConfigContextLoader.class)
public class ErrorReportTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LocalFileManager localFileManager;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private SftpFileManager sftpFileManager;

    private String sorterInputFolder;
    private String temporaryFiles;
    private int maxWaitingTime;

    @Parameters({"sorterInputFolder", "temporaryFiles", "maxWaitingTime"})
    @BeforeClass
    public void setUp(String sorterInputFolder, String temporaryFiles, int maxWaitingTime) {
        this.sorterInputFolder = sorterInputFolder;
        this.temporaryFiles = temporaryFiles;
        this.maxWaitingTime = maxWaitingTime;
    }

    @Parameters({"temporaryFiles"})
    @BeforeMethod
    @AfterMethod
    public void clean(String temporaryFiles) {
        errorRepository.deleteAll();
        localFileManager.cleanDirectories(false, sorterInputFolder);
        localFileManager.cleanDirectories(true, temporaryFiles);
        sftpFileManager.cleanDirectories("/errorFiles/valid/", "/errorFiles/notValid/");
    }

    @DataProvider
    public Object[][] errorReportSftpTest() {
        return new Object[][]{
                {"/testReports/error/valid/", "/errorFiles/valid/"},
                {"/testReports/error/notValid/", "/errorFiles/notValid/"}
        };
    }

    /**
     * Test sorter with error reports, and check it on sftp server
     *
     * @param fromFolder   folder from which copied files
     * @param remoteFolder sftp folder were sorter must put files
     */
    @Test(dataProvider = "errorReportSftpTest")
    public void errorReportsSorterOnSftpTest(String fromFolder, String remoteFolder) {
        List<File> errorReportsList = localFileManager.getFiles(fromFolder, true);
        localFileManager.copyFiles(errorReportsList, sorterInputFolder);
        Assert.assertTrue(localFileManager.waitFilesTransfer(sorterInputFolder, maxWaitingTime, true),
                "Files are not moved from sorter input folder " + sorterInputFolder);
        sftpFileManager.downloadFiles(remoteFolder, temporaryFiles, true);
        List<File> fromSftpFiles = localFileManager.getFiles(temporaryFiles, true);
        List<String> errorReportsFileNames = FileUtil.getFilesNames(errorReportsList);
        List<String> fromSftpFileNames = FileUtil.getFilesNames(fromSftpFiles);
        Assert.assertFalse(fromSftpFiles.isEmpty(), "List of files received from sftp is empty");
        Assert.assertEquals(errorReportsFileNames, fromSftpFileNames,
                "Names from files received from sftp and shipped to sorter are not equals");
    }
}
