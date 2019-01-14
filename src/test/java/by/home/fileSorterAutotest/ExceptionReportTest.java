package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.LocalFileManager;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class with tests for exception files sorter
 */
public class ExceptionReportTest {

    private LocalFileManager localFileManager;
    private String sorterInputFolder;
    private String exceptionFolder;
    private int maxWaitingTime;

    @Parameters({"sorterInputFolder", "exceptionFolder", "maxWaitingTime"})
    @BeforeClass
    public void setUp(String sorterInputFolder, String exceptionFolder, String maxWaitingTime) {
        this.localFileManager = new LocalFileManager();
        this.sorterInputFolder = sorterInputFolder;
        this.exceptionFolder = exceptionFolder;
        this.maxWaitingTime = Integer.parseInt(maxWaitingTime);
    }

    @DataProvider
    public Object[][] exceptionReportTest() {
        return new Object[][]{
                {"/testReports/exception/valid/", exceptionFolder + "valid/"},
                {"/testReports/exception/notValid/", exceptionFolder + "notValid/"},
        };
    }

    /**
     * Test sorter with exception files
     *
     * @param targetFolder folder from which get test files
     * @param sorterFolder folder were sorter must put processed files
     */
    @Test(dataProvider = "exceptionReportTest")
    public void txtFilesSorterTest(String targetFolder, String sorterFolder) {
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
}
