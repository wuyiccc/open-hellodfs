package com.wuyiccc.hellodfs.admin.config;

import com.wuyiccc.hellodfs.admin.common.enumeration.ResponseStatusEnum;
import com.wuyiccc.hellodfs.admin.common.exception.ThrowException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wuyiccc
 * @date 2020/10/28 21:07
 * 单独的日期格式转化
 */
@Configuration
public class DateConverterConfig implements Converter<String, Date> {

    private static final List<String> formatterList = new ArrayList<>(4);
    static{
        formatterList.add("yyyy-MM");
        formatterList.add("yyyy-MM-dd");
        formatterList.add("yyyy-MM-dd hh:mm");
        formatterList.add("yyyy-MM-dd hh:mm:ss");
    }

    @Override
    public Date convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if(source.matches("^\\d{4}-\\d{1,2}$")){
            return parseDate(source, formatterList.get(0));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")){
            return parseDate(source, formatterList.get(1));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formatterList.get(2));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formatterList.get(3));
        }else {
            ThrowException.display(ResponseStatusEnum.SYSTEM_DATE_PARSER_ERROR);
        }
        return null;
    }

    /**
     * 日期转换方法
     * @param dateStr
     * @param formatter
     * @return
     */
    public Date parseDate(String dateStr, String formatter) {
        Date date=null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(formatter);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


}
