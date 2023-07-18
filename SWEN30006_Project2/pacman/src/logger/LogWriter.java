package logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class LogWriter {

    public FileWriter fileWriter;

    public LogWriter(String filePath) {
        try {
            this.fileWriter = new FileWriter(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeString(String str) {
        try {
            fileWriter.write(str);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
