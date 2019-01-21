package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.config.AppConfig;
import by.home.fileSorterAutotest.repository.ExceptionRepository;
import by.home.fileSorterAutotest.service.LocalFileManager;
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
    public Object[][] exceptionReportTest() {
        return new Object[][]{
                {"/testReports/exception/valid/", validExceptionProcessedFolder},
                {"/testReports/exception/notValid/", notValidExceptionProcessedFolder}
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
        localFileManager.copyFiles(exceptionReportList, sorterInputFolder);
        Assert.assertFalse(localFileManager.waitFilesTransfer(sorterFolder, maxWaitingTime, false),
                "Files not found to output sorter folder");
        List<File> processedFiles = localFileManager.getFiles(sorterFolder, false);
        List<String> exceptionReportFileNames = exceptionReportList.stream().map(File::getName).collect(Collectors.toList());
        List<String> processedFilesNames = processedFiles.stream().map(File::getName).collect(Collectors.toList());
        Assert.assertFalse(processedFiles.isEmpty(), "Not found needed files in output sorter folder " + sorterFolder);
        Assert.assertEquals(exceptionReportFileNames, processedFilesNames,
                "Names of files received from output sorter folder and input files are not equals");
    }
}
