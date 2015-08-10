package teamdivider.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

  public static String projectHeadPicturePath = PropertyUtil.PROJECT_IMAGE_PATH + "avatar/";

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
