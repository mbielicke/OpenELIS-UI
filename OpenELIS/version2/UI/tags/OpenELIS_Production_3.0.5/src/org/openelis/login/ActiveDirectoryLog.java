package org.openelis.login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActiveDirectoryLog {
    PrintStream ps;
    SimpleDateFormat logFormat, fileFormat;
    
    public ActiveDirectoryLog() {
        File logFile;
        String fileName;
        
        fileName = "/usr/local/java/jboss/standalone/log/ad.log";
        logFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fileFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        logFile = new File(fileName);
        if (logFile.exists()) {
            logFile.renameTo(new File(fileName + "." + fileFormat.format(new Date())));
            logFile = new File(fileName);
        }
        
        try {
            ps = new PrintStream(logFile);
            logFile.setReadable(false, false);
            logFile.setReadable(true);
            logFile.setWritable(false, false);
            logFile.setWritable(true);
            logFile.setExecutable(false, false);
        } catch (FileNotFoundException fnfE) {
            fnfE.printStackTrace();
        }
    }
    
    public void log(String userName, String password) {
        Date now;
        
        now = new Date();
        ps.println(logFormat.format(now) + "|" + userName + "|" + password);
    }
}
