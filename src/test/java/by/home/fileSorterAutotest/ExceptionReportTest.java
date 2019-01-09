package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.LocalFileManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Class with tests for exception files sorter
 */
@Slf4j
public class ExceptionReportTest {

    private LocalFileManager localFileManager;
    private String sorterInputFolder;
    private String exceptionFolder;

    @Parameters({"sorterInputFolder", "exceptionFolder"})
    @BeforeClass
    public void setUp(String sorterInputFolder, String exceptionFolder) {
        this.localFileManager = new LocalFileManager();
        this.sorterInputFolder = sorterInputFolder;
        this.exceptionFolder = exceptionFolder;
    }

    @DataProvider
    public Object[][] exceptionReportTest() {
        log.info("Starting data provider");
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
        List<File> testFileList = localFileManager.getFiles(targetFolder, true);
        localFileManager.copy(testFileList, sorterInputFolder);
        localFileManager.waitFilesTransfer(sorterFolder);
        List<File> processedFiles = localFileManager.getFiles(sorterFolder, false);
        Assert.assertFalse(processedFiles.isEmpty(), "Not found needed files in folder " + sorterFolder);
        Assert.assertNotEquals(testFileList, processedFiles, "Files received from sftp are not equals");
    }


}
