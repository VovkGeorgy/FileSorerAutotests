package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.FileGetter;
import by.home.fileSorterAutotest.service.IFileMover;
import by.home.fileSorterAutotest.service.FileMover;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;

@Slf4j
public class TxtFilesTest {

    private FileGetter fileGetter;
    private IFileMover fileMover;
    private String validTxtFromFolder;
    private String notValidTxtFromFolder;
    private String notSortedFolderPath;
    private String validTxtRemoteFolder;
    private String notValidTxtRemoteFolder;

    @Parameters({"validTxtFromFolder", "notValidTxtFromFolder", "notSortedFolderPath", "validTxtRemoteFolder", "notValidTxtRemoteFolder"})
    @BeforeClass
    public void setUp(String validTxtFromFolder, String notValidTxtFromFolder, String notSortedFolderPath,
                      String validTxtRemoteFolder, String notValidTxtRemoteFolder) throws Exception {
        log.info("Getting parameters from xml");
        this.fileGetter = new FileGetter();
        this.fileMover = new FileMover();
        this.validTxtFromFolder = validTxtFromFolder;
        this.notValidTxtFromFolder = notValidTxtFromFolder;
        this.notSortedFolderPath = notSortedFolderPath;
        this.validTxtRemoteFolder = validTxtRemoteFolder;
        this.notValidTxtRemoteFolder = notValidTxtRemoteFolder;
    }

    @DataProvider
    public Object[][] txtSorterTest() {
        log.info("Starting data provider");
        return new Object[][]{
                {validTxtFromFolder, notSortedFolderPath, validTxtRemoteFolder},
                {notValidTxtFromFolder, notSortedFolderPath, notValidTxtRemoteFolder},
        };
    }

    /**
     * Test sorter with txt files
     *
     * @param fromFolder      folder from which copied files
     * @param notSortedFolder folder were working file sorter
     * @param remoteFolder    folder were sorter put files
     */
    @Test(dataProvider = "txtSorterTest")
    public void txtFilesSorterTest(String fromFolder, String notSortedFolder, String remoteFolder) {
        log.info("Begin json file test");
        log.debug("Getting files from local storage");
        List<File> testFileList = fileGetter.getFiles(fromFolder);
        log.debug("Move files to file sorter working folder");
        fileMover.move(testFileList, fromFolder, notSortedFolder);
        try {
            log.info("Wait when file sorter working");
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("Check that file are moved from sorter working folder");
        testFileList.stream().map(file -> new File(notSortedFolder + file.getName())).map(File::exists).forEach(Assert::assertFalse);
        log.debug("Get files from folder were sorter must put them");
        List<File> gatedFromRemoteFolderFiles = fileGetter.getFiles(remoteFolder);
        log.debug("Check that local storage is not empty");
        Assert.assertFalse(gatedFromRemoteFolderFiles.isEmpty());
        log.debug("Check that given and gated files are equals");
        Assert.assertNotEquals(testFileList, gatedFromRemoteFolderFiles);
        log.info("End test");
    }
}
