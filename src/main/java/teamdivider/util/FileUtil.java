package teamdivider.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

  // specific for tomcat server
  public static String projectAvatarPath = new File("").getAbsolutePath()
      .replace("bin", "temp/");

  public static void FileCopy(InputStream input, String writeFile) {
    try {
      FileOutputStream output = new FileOutputStream(writeFile);
      int read = input.read();
      while (read != -1) {
        output.write(read);
        read = input.read();
      }
      input.close();
      output.close();
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
