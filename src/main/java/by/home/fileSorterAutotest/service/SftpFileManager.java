package by.home.fileSorterAutotest.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Class have method those get files from sftp
 */
@Slf4j
public class SftpFileManager {
    private JSch jsch = new JSch();
    private Session session = null;
    private ChannelSftp sftpChannel = new ChannelSftp();

    /**
     * Download files from sftp server
     *
     * @param fileList      files which need get from sftp
     * @param fromFolder    folder were need find files
     * @param toFolder      folder where need put files
     * @param sftpConfigMap map of sftp config
     */
    public void getFilesFromSftp(List<File> fileList, String fromFolder, String toFolder, Map<String, String> sftpConfigMap) {
        try {
            ChannelSftp sftpChannel = configSftpChannel(sftpConfigMap);
            log.info("Upload files to server");
            File folder = ResourceUtils.getFile("classpath:" + toFolder);
            for (File file : fileList) {
                sftpChannel.get(fromFolder + file.getName(), folder.getPath() + "/" + file.getName());
            }
        } catch (JSchException | SftpException e) {
            log.error("SFTP Connection exception {}", e.getMessage());
        } catch (NullPointerException | FileNotFoundException nul) {
            log.debug("Something are not found", nul.getMessage());
        } finally {
            log.info("Close server connection");
            sftpChannel.exit();
            if (session != null) session.disconnect();
            else log.debug("Connection session is NULL");
        }
    }

    private ChannelSftp configSftpChannel(Map<String, String> sftpConfigMap) throws JSchException {
        session = jsch.getSession(sftpConfigMap.get("ftpUsername"), sftpConfigMap.get("ftpHost"), Integer.parseInt(sftpConfigMap.get("ftpPort")));
        log.info("Try to connect to server");
        session.setConfig(sftpConfigMap.get("ftpHostKeyChecking"), sftpConfigMap.get("ftpHostKeyCheckingValue"));
        session.setPassword(sftpConfigMap.get("ftpPassword"));
        log.debug("Try to connect to session {}", session);
        session.connect();
        Channel channel = session.openChannel(sftpConfigMap.get("ftpChanelType"));
        log.debug("Try to connect to chanel {}", channel);
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        log.debug("Connecting to server is established");
        return sftpChannel;
    }
}
