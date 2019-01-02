package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.LocalFileManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;

/**
 * Class with tests for txt files sorter
 */
@Slf4j
public class TxtFilesTest {

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
    public Object[][] txtSorterTest() {
        log.info("Starting data provider");
        return new Object[][]{
                {"/testFiles/txt/valid/", exceptionFolder + "valid/"},
                {"/testFiles/txt/notValid/", exceptionFolder + "notValid/"},
        };
    }

    /**
     * Test sorter with txt files
     *
     * @param fromFolder   folder from which get test files
     * @param sorterFolder folder were sorter must put processed files
     */
    @Test(dataProvider = "txtSorterTest", timeOut = 8000)
    public void txtFilesSorterTest(String fromFolder, String sorterFolder) {
        List<File> testFileList = localFileManager.getFiles(fromFolder, true);
        localFileManager.move(testFileList, sorterInputFolder);
        localFileManager.waitFilesTransfer(testFileList, sorterInputFolder);
        List<File> processedFiles = localFileManager.getFiles(sorterFolder, false);
        Assert.assertFalse(processedFiles.isEmpty(), "Not found needed files in folder " + sorterFolder);
        Assert.assertNotEquals(testFileList, processedFiles, "Files received from sftp are not equals");
    }


}
