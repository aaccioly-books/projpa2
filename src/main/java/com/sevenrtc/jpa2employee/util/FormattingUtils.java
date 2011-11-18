package com.sevenrtc.jpa2employee.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Anthony Accioly <a.accioly at 7rtc.com>
 */
public class FormattingUtils {
    
    private static final Locale BR = new Locale("pt", "BR");
    
    private static final DateFormat FORMATO_DATA = 
            DateFormat.getDateInstance(DateFormat.MEDIUM, BR);
    
    public static String formatarData(Date data) {    
        return data != null ? FORMATO_DATA.format(data) : null;
    }
    
    public static String formatarData(Calendar data) {
        return data != null ? FORMATO_DATA.format(data.getTime()) : null;
    }
}
