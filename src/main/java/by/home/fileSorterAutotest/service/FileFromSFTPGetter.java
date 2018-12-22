package by.home.fileSorterAutotest.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Class have method those get files from sftp
 */
@Slf4j
@Service
public class FileFromSFTPGetter {
    private String ftpUsername = "admin";
    private String ftpPassword = "11111";
    private String ftpHost = "0.0.0.0";
    private Integer ftpPort = 22;
    private String ftpConfigV1 = "StrictHostKeyChecking";
    private String ftpConfigV2 = "no";
    private String ftpChanelType = "sftp";

    /**
     * Download files from sftp server
     *
     * @param fileList   files which need get from sftp
     * @param fromFolder folder were need find files
     * @param toFolder   folder where need put files
     */
    public void getFilesFromSftp(List<File> fileList, String fromFolder, String toFolder) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = new ChannelSftp();
        try {
            session = jsch.getSession(ftpUsername, ftpHost, ftpPort);
            log.info("Try to connect to server");
            session.setConfig(ftpConfigV1, ftpConfigV2);
            session.setPassword(ftpPassword);
            log.debug("Try to connect to session {}", session);
            session.connect();
            Channel channel = session.openChannel(ftpChanelType);
            log.debug("Try to connect to chanel {}", channel);
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            log.debug("Connecting to server is established");
            log.info("Upload files to server");
            for (File file : fileList) {
                sftpChannel.get(fromFolder + file.getName(), toFolder + file.getName());
            }
        } catch (JSchException | SftpException e) {
            log.error("SFTP Connection exception {}", e.getMessage());
        } catch (NullPointerException nul) {
            log.debug("Connection session is NULL", nul.getMessage());
        } finally {
            log.info("Close server connection");
            sftpChannel.exit();
            if (session != null) session.disconnect();
            else log.debug("Connection session is NULL");
        }
    }
}
