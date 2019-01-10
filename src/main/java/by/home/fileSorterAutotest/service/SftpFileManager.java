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
    private ResourcesUtil resourcesUtil = new ResourcesUtil();

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
     * @param fileList            files which need get from sftp
     * @param fromFolderPath      folder path where need find files
     * @param toFolderPath        folder path where need put files
     * @param downloadToResources show that files must downloaded to resources
     */
    public void downloadFilesFromSftp(List<File> fileList, String fromFolderPath, String toFolderPath, boolean
            downloadToResources) {
        try {
            toFolderPath = downloadToResources ? resourcesUtil.getResourcesPath(toFolderPath) :
                    toFolderPath;
            ChannelSftp sftpChannel = configSftpChannel(sftpConfigMap);
            log.info("Download {} files from server", fileList.size());
            for (File file : fileList) {
                String fileName = file.getName();
                log.debug("Download file {} from folder {}, to folder {}", fileName, fromFolderPath,
                        toFolderPath);
                sftpChannel.get(fromFolderPath + fileName, toFolderPath + fileName);
            }
        } catch (JSchException | SftpException | NullPointerException e) {
            log.error("SFTP Connection process exception {}", e.getMessage());
        } finally {
            log.info("Close server connection");
            connectionTeardown();
        }
    }

    private ChannelSftp configSftpChannel(Map<String, String> sftpConfigMap) throws JSchException {
        session = new JSch().getSession(sftpConfigMap.get("ftpUsername"), sftpConfigMap.get("ftpHost"), Integer.parseInt
                (sftpConfigMap.get("ftpPort")));
        session.setConfig(sftpConfigMap.get("ftpHostKeyChecking"), sftpConfigMap.get("ftpHostKeyCheckingValue"));
        session.setPassword(sftpConfigMap.get("ftpPassword"));
        session.connect();
        Channel channel = session.openChannel(sftpConfigMap.get("ftpChanelType"));
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        log.info("Connecting to {} : {} server is established", sftpConfigMap.get("ftpHost"), sftpConfigMap.get("ftpPort"));
        return sftpChannel;
    }

    private void connectionTeardown() {
        sftpChannel.exit();
        if (session != null) session.disconnect();
        else log.debug("Cant close session, is already NULL");
    }
}