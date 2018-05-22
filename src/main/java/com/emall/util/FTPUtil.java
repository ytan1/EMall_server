package com.emall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sun.net.ftp.FtpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String hostIp = PropertyUtil.getValue("ftp.server.ip");
    private static String user = PropertyUtil.getValue("ftp.user");
    private static String pass = PropertyUtil.getValue("ftp.pass");
    public static boolean upload(File file) {
        boolean result = false;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(hostIp);
        } catch (IOException e) {
            logger.error("ftpClient connect error", e);
            e.printStackTrace();
        }
        boolean isLogin = false;
        try {
            isLogin = ftpClient.login(user, pass);
            if(isLogin){
                result = uploadFile(file, ftpClient);
            }
        } catch (IOException e) {
            logger.error("Error when ftp login", e);
            e.printStackTrace();
        }

        return result;
    }

    private static boolean uploadFile(File file, FTPClient ftpClient){
        boolean result2 = false;
        FileInputStream fis = null;
        try{
            //set the diretory in FTP server disk, where files are deployed online
            ftpClient.changeWorkingDirectory("img");
            //other settings for ftp
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            //upload to ftp server
            fis = new FileInputStream(file);
            ftpClient.storeFile(file.getName(), fis);//param:remotepath(name?) , stream
            result2 = true;
        }
        catch (IOException e){
            logger.error("Error ftpClient upload", e);
            e.printStackTrace();
        }finally {
            try {
                fis.close();
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("Error when close fileInputStream or disconnect ftpClient", e);
                e.printStackTrace();
            }


        }
        return result2;

    }
}
