package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.config.DataConfig;
import by.home.fileSorterAutotest.entity.ErrorMessage;
import by.home.fileSorterAutotest.repository.ErrorRepository;
import by.home.fileSorterAutotest.service.LocalFileManager;
import by.home.fileSorterAutotest.service.SftpFileManager;
import by.home.fileSorterAutotest.service.report.IReportParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static by.home.fileSorterAutotest.service.LocalFileManager.FOLDER_MUST_BE_EMPTY;

@Test
@ContextConfiguration(classes = DataConfig.class, loader = AnnotationConfigContextLoader.class)
public class ErrorReportDatabaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LocalFileManager localFileManager;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private IReportParser<ErrorMessage> jsonParser;

    @Autowired
    private SftpFileManager sftpFileManager;

    private String sorterInputFolder;
    private int maxWaitingTime;

    @Parameters({"sorterInputFolder", "temporaryFolder", "maxWaitingTime"})
    @BeforeClass
    public void setUp(String sorterInputFolder, int maxWaitingTime) {
        this.sorterInputFolder = sorterInputFolder;
        this.maxWaitingTime = maxWaitingTime;
    }

    @Parameters({"temporaryFolder"})
    @BeforeMethod
    @AfterMethod
    public void clean(String temporaryFolder) {
        errorRepository.deleteAll();
        localFileManager.cleanDirectories(true, temporaryFolder);
        localFileManager.cleanDirectories(false, sorterInputFolder);
        sftpFileManager.cleanDirectories("/errorFiles/valid/", "/errorFiles/notValid/");
    }

    @DataProvider
    public Object[][] errorReportDatabaseTest() {
        return new Object[][]{
                {"/testReports/error/valid/"}
        };
    }

    /**
     * Test sorter saving valid error report message entities in database
     *
     * @param fromFolder folder with valid reports
     */
    @Test(dataProvider = "errorReportDatabaseTest")
    public void errorReportsSorterInBaseTest(String fromFolder) {
        List<File> errorReportsList = localFileManager.getFiles(fromFolder, true);
        localFileManager.copyFiles(errorReportsList, sorterInputFolder);
        List<ErrorMessage> errorMessageList = errorReportsList.stream().map(file -> jsonParser.parseFile(file)).collect(Collectors
                .toList());
        Assert.assertTrue(localFileManager.waitFilesTransfer(sorterInputFolder, maxWaitingTime, FOLDER_MUST_BE_EMPTY),
                "Files are not moved from sorter input folder " + sorterInputFolder);
        List<ErrorMessage> fromDatabaseList = (List<ErrorMessage>) errorRepository.findAll();
        Assert.assertEquals(errorMessageList, fromDatabaseList, "Lists of messages entity are not equals");
    }
}
