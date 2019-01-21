package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.config.AppConfig;
import by.home.fileSorterAutotest.entity.ExceptionMessage;
import by.home.fileSorterAutotest.repository.ExceptionRepository;
import by.home.fileSorterAutotest.service.LocalFileManager;
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

@Test
@ContextConfiguration(classes = AppConfig.class, loader = AnnotationConfigContextLoader.class)
public class ExceptionReportDatabaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LocalFileManager localFileManager;

    @Autowired
    private ExceptionRepository exceptionRepository;

    @Autowired
    private IReportParser<ExceptionMessage> csvParser;

    private String sorterInputFolder;
    private String validExceptionProcessedFolder;
    private String notValidExceptionProcessedFolder;
    private int maxWaitingTime;


    @Parameters({"sorterInputFolder", "exceptionProcessedFolder", "maxWaitingTime"})
    @BeforeClass
    public void setUp(String sorterInputFolder, String exceptionFolder, String maxWaitingTime) {
        this.sorterInputFolder = sorterInputFolder;
        this.validExceptionProcessedFolder = exceptionFolder + "valid/";
        this.notValidExceptionProcessedFolder = exceptionFolder + "notValid/";
        this.maxWaitingTime = Integer.parseInt(maxWaitingTime);
    }

    @Parameters({"exceptionProcessedFolder"})
    @BeforeMethod
    @AfterMethod
    public void clean(String temporaryFiles) {
        exceptionRepository.deleteAll();
        localFileManager.cleanDirectories(false, validExceptionProcessedFolder, notValidExceptionProcessedFolder,
                sorterInputFolder);
    }

    @DataProvider
    public Object[][] exceptionReportDatabaseTest() {
        return new Object[][]{
                {"/testReports/exception/valid/"}
        };
    }

    /**
     * Test sorter saving valid exception report message entities in database
     *
     * @param fromFolder folder with valid reports
     */
    @Test(dataProvider = "exceptionReportDatabaseTest")
    public void exceptionReportsSorterInBaseTest(String fromFolder) {
        List<File> errorReportsList = localFileManager.getFiles(fromFolder, true);
        localFileManager.copyFiles(errorReportsList, sorterInputFolder);
        List<ExceptionMessage> errorMessageList = errorReportsList.stream().map(file -> csvParser.parseFile(file)).collect
                (Collectors.toList());
        Assert.assertTrue(localFileManager.waitFilesTransfer(sorterInputFolder, maxWaitingTime, true),
                "Files are not moved from sorter input folder " + sorterInputFolder);
        List<ExceptionMessage> fromDatabaseList = errorMessageList.stream().map(message -> exceptionRepository.findById(message
                .getId()
        )).map(message -> message.orElseGet(null)).collect(Collectors.toList());
        Assert.assertTrue(errorMessageList.equals(fromDatabaseList), "Lists of messages entity are not equals");
    }
}
