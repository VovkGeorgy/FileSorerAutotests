package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.FileFromSFTPGetter;
import by.home.fileSorterAutotest.service.FileGetter;
import by.home.fileSorterAutotest.service.IFileMover;
import by.home.fileSorterAutotest.service.FileMover;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;

/**
 * Class test sorter of files
 */
@Slf4j
public class JsonFilesTest {

    private FileGetter fileGetter;
    private IFileMover fileMover;
    private FileFromSFTPGetter fileFromSFTPGetter;
    private String validJsonFromFolder;
    private String notSortedFolderPath;
    private String fromSftpStorage;
    private String jsonSftpValidFolderPath;
    private String notValidJsonFromFolder;
    private String jsonSftpNotValidFolderPath;

    @Parameters({"validJsonFromFolder", "notSortedFolderPath", "fromSftpStorage", "jsonSftpValidFolderPath", "notValidJsonFromFolder", "jsonSftpNotValidFolderPath"})
    @BeforeClass
    public void setUp(String validJsonFromFolder, String notSortedFolderPath,
                      String fromSftpStorage, String jsonSftpValidFolderPath,
                      String notValidJsonFromFolder, String jsonSftpNotValidFolderPath) throws Exception {
        log.info("Getting parameters from xml");
        fileGetter = new FileGetter();
        fileMover = new FileMover();
        fileFromSFTPGetter = new FileFromSFTPGetter();
        this.validJsonFromFolder = validJsonFromFolder;
        this.notSortedFolderPath = notSortedFolderPath;
        this.fromSftpStorage = fromSftpStorage;
        this.jsonSftpValidFolderPath = jsonSftpValidFolderPath;
        this.notValidJsonFromFolder = notValidJsonFromFolder;
        this.jsonSftpNotValidFolderPath = jsonSftpNotValidFolderPath;
    }

    @DataProvider
    public Object[][] jsonSorterTest() {
        log.info("Starting data provider");
        return new Object[][]{
                {validJsonFromFolder, notSortedFolderPath, fromSftpStorage, jsonSftpValidFolderPath},
                {notValidJsonFromFolder, notSortedFolderPath, fromSftpStorage, jsonSftpNotValidFolderPath},
        };
    }

    /**
     * Test sorter with json files
     *
     * @param fromFolder      folder from which copied files
     * @param notSortedFolder folder were working file sorter
     * @param fromSftpStorage sftp server were we must find our files
     * @param remoteFolder    sftp folder were sorter put files
     */
    @Test(dataProvider = "jsonSorterTest")
    public void jsonFilesSorterTest(String fromFolder, String notSortedFolder,
                                    String fromSftpStorage, String remoteFolder) {
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
        log.debug("Get files from sftp to local storage");
        fileFromSFTPGetter.getFilesFromSftp(testFileList, remoteFolder, fromSftpStorage);
        List<File> getedFromSftpFiles = fileGetter.getFiles(fromSftpStorage);
        log.debug("Check that local storage is not empty");
        Assert.assertFalse(getedFromSftpFiles.isEmpty());
        log.debug("Check that given and gated files are equals");
        Assert.assertNotEquals(testFileList, getedFromSftpFiles);
        log.info("End test");
    }
}
