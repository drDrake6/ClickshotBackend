package step.learning.services;

import com.google.inject.Singleton;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class LoggerService{
    public enum Status {INFO, ERROR}
    Map <Status, String> statusLabels;

    public LoggerService(){
        statusLabels = new HashMap<>();
        statusLabels.put(Status.INFO, "[INFO] ");
        statusLabels.put(Status.ERROR, "[ERROR] ");
    }

    public void log(String log, Status status){
        System.out.println(statusLabels.get(status) + log);
        try(FileOutputStream fos = new FileOutputStream("clickshotLogs.txt", true)) {
            fos.write((statusLabels.get(status) + log + "\r\n").getBytes());
        }
        catch( Exception ex ) {
            System.out.println("LoggerService error: " + ex.getMessage());
        }
    }
}
