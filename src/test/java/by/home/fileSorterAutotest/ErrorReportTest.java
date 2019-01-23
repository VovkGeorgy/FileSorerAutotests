package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.config.DataConfig;
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

import static by.home.fileSorterAutotest.service.LocalFileManager.FOLDER_MUST_BE_EMPTY;

/**
 * Class test report sorter
 */
@Test
@ContextConfiguration(classes = DataConfig.class, loader = AnnotationConfigContextLoader.class)
public class ErrorReportTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LocalFileManager localFileManager;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private SftpFileManager sftpFileManager;

    private String sorterInputFolder;
    private String temporaryFolder;
    private int maxWaitingTime;

    @Parameters({"sorterInputFolder", "temporaryFolder", "maxWaitingTime"})
    @BeforeClass
    public void setUp(String sorterInputFolder, String temporaryFolder, int maxWaitingTime) {
        this.sorterInputFolder = sorterInputFolder;
        this.temporaryFolder = temporaryFolder;
        this.maxWaitingTime = maxWaitingTime;
    }

    @Parameters({"temporaryFolder"})
    @BeforeMethod
    @AfterMethod
    public void clean(String temporaryFolder) {
        errorRepository.deleteAll();
        localFileManager.cleanDirectories(false, sorterInputFolder);
        localFileManager.cleanDirectories(true, temporaryFolder);
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
        Assert.assertTrue(localFileManager.waitFilesTransfer(sorterInputFolder, maxWaitingTime, FOLDER_MUST_BE_EMPTY),
                "Files are not moved from sorter input folder " + sorterInputFolder);
        sftpFileManager.downloadFiles(remoteFolder, temporaryFolder, true);
        List<File> fromSftpFiles = localFileManager.getFiles(temporaryFolder, true);
        List<String> errorReportsFileNames = FileUtil.getFilesNames(errorReportsList);
        List<String> fromSftpFileNames = FileUtil.getFilesNames(fromSftpFiles);
        Assert.assertFalse(fromSftpFiles.isEmpty(), "List of files received from sftp is empty");
        Assert.assertEquals(errorReportsFileNames, fromSftpFileNames,
                "Names from files received from sftp and shipped to sorter are not equals");
    }
}
