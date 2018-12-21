package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.service.FileGetter;
import by.home.fileSorterAutotest.service.IFileMover;
import by.home.fileSorterAutotest.service.FileMover;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.List;

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
        List<File> testFileList = fileGetter.getFiles(fromFolder);
        fileMover.move(testFileList, fromFolder, notSortedFolder);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testFileList.stream().map(file -> new File(notSortedFolder + file.getName())).map(File::exists).forEach(Assert::assertFalse);
        List<File> gatedFromRemoteFolderFiles = fileGetter.getFiles(remoteFolder);
        Assert.assertFalse(gatedFromRemoteFolderFiles.isEmpty());
        Assert.assertNotEquals(testFileList, gatedFromRemoteFolderFiles);
    }
}
