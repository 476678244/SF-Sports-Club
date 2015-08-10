package teamdivider.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

  public static Date praseToGMT8(Date gMTTime) throws ParseException {
    DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
        Locale.CHINA);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    String goTimeString = format.format(gMTTime);
    format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    return format.parse(goTimeString);

  }
}
