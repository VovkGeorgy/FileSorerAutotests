package by.home.fileSorterAutotest.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class have method those get files from sftp
 */
@Slf4j
public class SftpFileManager {
    private Session session = null;
    private ChannelSftp sftpChannel = new ChannelSftp();
    private Map<String, String> sftpConfigMap = new HashMap<>();

    public SftpFileManager(String ftpUsername, String ftpPassword, String ftpHost, String ftpPort,
                           String ftpHostKeyChecking, String ftpHostKeyCheckingValue, String ftpChanelType) {
        sftpConfigMap.put("ftpUsername", ftpUsername);
        sftpConfigMap.put("ftpPassword", ftpPassword);
        sftpConfigMap.put("ftpHost", ftpHost);
        sftpConfigMap.put("ftpPort", ftpPort);
        sftpConfigMap.put("ftpHostKeyChecking", ftpHostKeyChecking);
        sftpConfigMap.put("ftpHostKeyCheckingValue", ftpHostKeyCheckingValue);
        sftpConfigMap.put("ftpChanelType", ftpChanelType);
    }

    /**
     * Move files from sftp server
     *
     * @param fileList   files which need get from sftp
     * @param fromFolder folder were need find files
     * @param toFolder   folder where need put files
     */
    public void moveFilesFromSftp(List<File> fileList, String fromFolder, String toFolder) {
        try {
            ChannelSftp sftpChannel = configSftpChannel(sftpConfigMap);
            log.info("Upload files to server");
            File folder = new File(this.getClass().getResource(toFolder).getFile());
            for (File file : fileList) {
                sftpChannel.get(fromFolder + file.getName(), folder.getPath() + "/" + file.getName());
            }
        } catch (JSchException | SftpException e) {
            log.error("SFTP Connection exception {}", e.getMessage());
        } finally {
            sftpChannel.exit();
            log.info("Close server connection");
            if (session != null) session.disconnect();
            else log.debug("Connection session is NULL");
        }
    }

    private ChannelSftp configSftpChannel(Map<String, String> sftpConfigMap) throws JSchException {
        session = new JSch().getSession(sftpConfigMap.get("ftpUsername"), sftpConfigMap.get("ftpHost"), Integer.parseInt(sftpConfigMap.get("ftpPort")));
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
