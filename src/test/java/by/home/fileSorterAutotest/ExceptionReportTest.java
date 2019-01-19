package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.config.AppConfig;
import by.home.fileSorterAutotest.entity.ExceptionMessage;
import by.home.fileSorterAutotest.repository.ExceptionRepository;
import by.home.fileSorterAutotest.service.LocalFileManager;
import by.home.fileSorterAutotest.service.report.impl.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class with tests for exception files sorter
 */
@Test
@ContextConfiguration(classes = AppConfig.class, loader = AnnotationConfigContextLoader.class)
public class ExceptionReportTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LocalFileManager localFileManager;

    @Autowired
    private ExceptionRepository exceptionRepository;

    @Autowired
    private CsvParser csvParser;

    private String sorterInputFolder;
    private String exceptionProcessedFolder;
    private int maxWaitingTime;


    @Parameters({"sorterInputFolder", "exceptionProcessedFolder", "maxWaitingTime"})
    @BeforeClass
    public void setUp(String sorterInputFolder, String exceptionFolder, String maxWaitingTime) {
        this.sorterInputFolder = sorterInputFolder;
        this.exceptionProcessedFolder = exceptionFolder;
        this.maxWaitingTime = Integer.parseInt(maxWaitingTime);
        localFileManager.cleanDirectories(false, sorterInputFolder);
    }

    @Parameters({"exceptionProcessedFolder"})
    @BeforeMethod
    @AfterMethod
    public void clean(String temporaryFiles) {
        exceptionRepository.deleteAll();
        localFileManager.cleanDirectories(false,
                exceptionProcessedFolder + "valid/", exceptionProcessedFolder + "notValid/");
    }

    @DataProvider
    public Object[][] exceptionReportTest() {
        return new Object[][]{
                {"/testReports/exception/valid/", exceptionProcessedFolder + "valid/"},
                {"/testReports/exception/notValid/", exceptionProcessedFolder + "notValid/"}
        };
    }

    @DataProvider
    public Object[][] exceptionReportDatabaseTest() {
        return new Object[][]{
                {"/testReports/exception/valid/"}
        };
    }

    /**
     * Test sorter with exception files
     *
     * @param targetFolder folder from which get test files
     * @param sorterFolder folder were sorter must put processed files
     */
    @Test(dataProvider = "exceptionReportTest")
    public void exceptionReportsSorterOnLocalTest(String targetFolder, String sorterFolder) {
        List<File> exceptionReportList = localFileManager.getFiles(targetFolder, true);
        localFileManager.copy(exceptionReportList, sorterInputFolder);
        Assert.assertFalse(localFileManager.waitFilesTransfer(sorterFolder, maxWaitingTime, false),
                "Files not found to output sorter folder");
        List<File> processedFiles = localFileManager.getFiles(sorterFolder, false);
        List<String> exceptionReportFileNames = exceptionReportList.stream().map(File::getName).collect(Collectors.toList());
        List<String> processedFilesNames = processedFiles.stream().map(File::getName).collect(Collectors.toList());
        Assert.assertFalse(processedFiles.isEmpty(), "Not found needed files in output sorter folder " + sorterFolder);
        Assert.assertEquals(exceptionReportFileNames, processedFilesNames,
                "Names of files received from output sorter folder and input files are not equals");
    }

    /**
     * Test sorter saving valid exception report message entities in database
     *
     * @param fromFolder folder with valid reports
     */
    @Test(dataProvider = "exceptionReportDatabaseTest")
    public void exceptionReportsSorterInBaseTest(String fromFolder) {
        List<File> errorReportsList = localFileManager.getFiles(fromFolder, true);
        localFileManager.copy(errorReportsList, sorterInputFolder);
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
