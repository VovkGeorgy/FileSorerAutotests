package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.LocalFileManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;

@Slf4j
public class TxtFilesTest {

    private LocalFileManager localFileManager;
    private String notSortedFolderPath;

    @Parameters({"notSortedFolderPath"})
    @BeforeClass
    public void setUp(String notSortedFolderPath) throws Exception {
        log.info("Getting parameters from xml");
        this.localFileManager = new LocalFileManager();
        this.notSortedFolderPath = notSortedFolderPath;
    }

    @DataProvider
    public Object[][] txtSorterTest() {
        log.info("Starting data provider");
        return new Object[][]{
                {"/testFiles/txt/valid/", "D:/Prog/TEMP/WARNINGTestingZone/fileSorter/txtFolder/valid/"},
                {"/testFiles/txt/notValid/", "D:/Prog/TEMP/WARNINGTestingZone/fileSorter/txtFolder/notValid/"},
        };
    }

    /**
     * Test sorter with txt files
     *
     * @param fromFolder   folder from which copied files
     * @param remoteFolder folder were sorter put files
     */
    @Test(dataProvider = "txtSorterTest")
    public void txtFilesSorterTest(String fromFolder, String remoteFolder) throws InterruptedException {
        List<File> testFileList = localFileManager.getResources(fromFolder);
        localFileManager.move(testFileList, notSortedFolderPath);
        while (isFilesExist(testFileList)) {
            log.debug("Wait when sorter move files from {}", notSortedFolderPath);
        }
        List<File> gatedFromRemoteFolderFiles = localFileManager.getFiles(remoteFolder);
        Assert.assertFalse(gatedFromRemoteFolderFiles.isEmpty(), "Not found needed files in folder were then put file sorter");
        Assert.assertNotEquals(testFileList, gatedFromRemoteFolderFiles, "Files gated from sftp are not equals");
    }

    private boolean isFilesExist(List<File> testFileList) {
        boolean filesExist = false;
        for (File file : testFileList) filesExist = new File(notSortedFolderPath + file.getName()).exists() || filesExist;
        return filesExist;
    }
}
