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
     * @param fileList     files which need get from sftp
     * @param fromFolder   folder were need find files
     * @param toFolderPath folder where need put files
     */
    public void downloadFilesFromSftp(List<File> fileList, String fromFolder, String toFolderPath) {
        try {
            ChannelSftp sftpChannel = configSftpChannel(sftpConfigMap);
            log.info("Download {} files from server", fileList.size());
            File toFolder = new File(this.getClass().getResource(toFolderPath).getFile());
            for (File file : fileList) {
                log.debug("Download file {} from server folder {}, to local folder {}", file.getName(), fromFolder, toFolderPath);
                sftpChannel.get(fromFolder + file.getName(), toFolder.getPath() + "/" + file.getName());
            }
        } catch (JSchException | SftpException | NullPointerException e) {
            log.error("SFTP Connection process exception {}", e.getMessage());
        } finally {
            sftpChannel.exit();
            log.info("Close server connection");
            if (session != null) session.disconnect();
            else log.debug("Cant close session, is already NULL");
        }
    }

    private ChannelSftp configSftpChannel(Map<String, String> sftpConfigMap) throws JSchException {
        log.info("Open server connection to {}", sftpConfigMap.get("ftpHost"));
        session = new JSch().getSession(sftpConfigMap.get("ftpUsername"), sftpConfigMap.get("ftpHost"), Integer.parseInt
                (sftpConfigMap.get("ftpPort")));
        session.setConfig(sftpConfigMap.get("ftpHostKeyChecking"), sftpConfigMap.get("ftpHostKeyCheckingValue"));
        session.setPassword(sftpConfigMap.get("ftpPassword"));
        session.connect();
        Channel channel = session.openChannel(sftpConfigMap.get("ftpChanelType"));
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        log.debug("Connecting to {} : {} server is established", sftpConfigMap.get("ftpHost"), sftpConfigMap.get("ftpPort"));
        return sftpChannel;
    }
}
