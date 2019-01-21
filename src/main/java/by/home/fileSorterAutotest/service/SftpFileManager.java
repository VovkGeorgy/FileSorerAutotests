package by.home.fileSorterAutotest.service;

import by.home.fileSorterAutotest.utils.FileUtil;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Vector;

/**
 * Class have method those get files from sftp
 */
@Slf4j
@Service
@PropertySource("classpath:sftp.properties")
public class SftpFileManager {

    @Value("${ftp.Username}")
    private String ftpUsername;

    @Value("${ftp.Password}")
    private String ftpPassword;

    @Value("${ftp.Host}")
    private String ftpHost;

    @Value("${ftp.Port}")
    private int ftpPort;

    @Value("${ftp.HostKeyChecking}")
    private String ftpHostKeyChecking;

    @Value("${ftp.HostKeyCheckingValue}")
    private String ftpHostKeyCheckingValue;

    @Value("${ftp.ChanelType}")
    private String ftpChanelType;

    private Session session;
    private ChannelSftp sftpChannel;

    @PostConstruct
    private void connectionInit() {
        try {
            log.info("Connecting to server");
            JSch jsch = new JSch();
            session = jsch.getSession(ftpUsername, ftpHost, ftpPort);
            session.setConfig(ftpHostKeyChecking, ftpHostKeyCheckingValue);
            session.setPassword(ftpPassword);
            session.connect();
            Channel channel = session.openChannel(ftpChanelType);
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            log.debug("Connecting to server is established");
        } catch (JSchException e) {
            sftpChannel = null;
            session = null;
            log.error("SFTP Connection exception {}", e.getMessage());
        }
    }

    @PreDestroy
    public void connectionTeardown() {
        log.info("Close server connection");
        if (session != null & sftpChannel != null) {
            sftpChannel.exit();
            session.disconnect();
        } else log.error("Connection statements is NULL");
    }

    /**
     * Download files from sftp server
     *
     * @param inputFolderPath     folder path where need find files
     * @param outputFolderPath    folder path where need download files
     * @param downloadToResources show that files must downloaded to resources
     */
    public void downloadFiles(String inputFolderPath, String outputFolderPath, boolean downloadToResources) {
        try {
            outputFolderPath = downloadToResources ? FileUtil.getResourcesPath(outputFolderPath) :
                    outputFolderPath;
            log.debug("Download files from folder {}, to folder {}", inputFolderPath, outputFolderPath);
            sftpChannel.cd(inputFolderPath);
            Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");
            for (ChannelSftp.LsEntry listEntry : list) {
                sftpChannel.get(listEntry.getFilename(), outputFolderPath);
            }
        } catch (SftpException | NullPointerException e) {
            log.error("SFTP Connection process exception {}", e.getMessage());
            connectionTeardown();
            connectionInit();
        }
    }

    /**
     * Method clean target directories on sftp
     *
     * @param directoryPaths paths of cleaning directories
     */
    public void cleanDirectories(String... directoryPaths) {
        try {
            for (String directoryPath : directoryPaths) {
                log.info("Delete all files from folder {}", directoryPath);
                sftpChannel.rm(directoryPath + "*");
            }
        } catch (SftpException | NullPointerException e) {
            log.error("SFTP Connection process exception {}", e.getMessage());
            connectionTeardown();
            connectionInit();
        }
    }
}