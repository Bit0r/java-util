package util;



import annotation.ExcelCheck;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * excel 工具类
 * @author zc
 */
public class ExcelUtils {
    /**
     * 正则表达式：否为整数或者浮点数，涵盖负数
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");



    private static Logger log = LoggerFactory.getLogger(ExcelUtils.class);


    /**
     * java对象转map
     * @param obj
     * @return
     */
    public static Map<?, ?> objectToMap(Object obj) {
        Map userMap = new HashMap();
        if (obj != null) {
            userMap = JSON.parseObject(JSON.toJSONString(obj), new TypeReference<Map<String, String>>() {
            });
        }
        return userMap;
    }

    /**
     * java对象转map
     * @param objList
     * @return
     */
    public static List<Map<String, Object>> objectToMapList(List objList) {
        List<Map<String, Object>> objMapList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(objList)) {
            objMapList = JSONArray.parseObject(JSONArray.parseArray(JSON.toJSONString(objList)).toJSONString(), List.class);
        }
        return objMapList;
    }

    /**
     * excel 对象数据校验
     * @param excelList
     * @param clazz
     * @return
     */
    public static List<String> excelCheckObj(List<?> excelList, Class<?> clazz) {
        List<String> errList = new ArrayList<>();
        Map<String, ExcelCheck> excelCheckMap = new HashMap<String, ExcelCheck>();
        Map<String, ExcelProperty> excelPropertyMap = new HashMap<String, ExcelProperty>();
        for (Field field : TableInfoHelper.getAllFields(clazz)) {
            ExcelCheck excelCheck = field.getAnnotation(ExcelCheck.class);
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelCheck != null) {
                excelCheckMap.put(field.getName(), excelCheck);
                excelPropertyMap.put(field.getName(), excelProperty);
            }
        }
        for (int i = 0; i < excelList.size(); i++) {
            Object obj = excelList.get(i);
            PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(obj);
            for (int j = 0; j < origDescriptors.length; j++) {
                String k = origDescriptors[j].getName();
                Object v = null;
                try {
                    v = PropertyUtils.getSimpleProperty(obj, k);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                ExcelCheck excelCheck = excelCheckMap.get(k);
                ExcelProperty excelProperty = excelPropertyMap.get(k);
                if (ObjectUtil.isNotEmpty(excelCheck) && ObjectUtil.isNotEmpty(excelProperty)) {
                    String fieldName = Arrays.toString(excelProperty.value());
                    fieldName = fieldName.substring(1, fieldName.length() - 1);

                    //不能为空
                    isNotNull(v, excelCheck, i, fieldName, errList);

                    //整数长度
                    integer(v, excelCheck, i, fieldName, errList);

                    //小数长度
                    decimal(v, excelCheck, i, fieldName, errList);

                    //时间格式
                    dateFormat(v, excelCheck, i, fieldName, errList);

                    //正则校验
                    matches(v, excelCheck, i, fieldName, errList);

                    //最大长度校验
                    maxLength(v, excelCheck, i, fieldName, errList);
                }
            }
        }
        return errList;
    }


    /**
     * 最大长度校验
     * @param v 值
     * @param excelCheck 注解
     * @param i 行数
     * @param fieldName excel段名
     * @param errList 错误信息集合
     */
    private static void maxLength(Object v, ExcelCheck excelCheck, int i, String fieldName, List<String> errList) {
        if (ObjectUtil.isNotEmpty(v) && excelCheck.maxLength() != -1 && v.toString().length() > excelCheck.maxLength()) {
            StringBuilder err = new StringBuilder("第").append(i).append("行，").append(fieldName).append("长度超过：").append(excelCheck.maxLength()).append(";");
            errList.add(err.toString());
        }
    }

    /**
     * 正则校验
     * @param v 值
     * @param excelCheck 注解
     * @param i 行数
     * @param fieldName excel段名
     * @param errList 错误信息集合
     */
    private static void matches(Object v, ExcelCheck excelCheck, int i, String fieldName, List<String> errList) {
        if (ObjectUtil.isNotEmpty(v) && ObjectUtil.isNotEmpty(excelCheck.matches())
                && !Pattern.compile(excelCheck.matches()).matcher(v.toString()).matches()) {
            StringBuilder err = new StringBuilder("第").append(i).append("行，").append(fieldName).append("格式不合法；");
            errList.add(err.toString());
        }
    }

    /**
     * 时间格式
     * @param v 值
     * @param excelCheck 注解
     * @param i 行数
     * @param fieldName excel段名
     * @param errList 错误信息集合
     */
    private static void dateFormat(Object v, ExcelCheck excelCheck, int i, String fieldName, List<String> errList) {
        if (ObjectUtil.isEmpty(excelCheck.dateFormat()) || v instanceof Date || ObjectUtil.isNotEmpty(v)) {
            return;
        }
        if (!isDate(v.toString(), excelCheck.dateFormat(), excelCheck.dateFormat().length())) {
            StringBuilder err = new StringBuilder("第").append(i).append("行，").append(fieldName).append("时间格式不是：").append(excelCheck.dateFormat()).append("；");
            errList.add(err.toString());
        }
    }

    /**
     * 小数长度
     * @param v 值
     * @param excelCheck 注解
     * @param i 行数
     * @param fieldName excel段名
     * @param errList 错误信息集合
     */
    private static void decimal(Object v, ExcelCheck excelCheck, int i, String fieldName, List<String> errList) {
        if (excelCheck.decimal() != -1 && ObjectUtil.isNotEmpty(v)) {
            if (isNumeric(v.toString())) {
                //去除小数末位的0
                BigDecimal value = new BigDecimal(v.toString());
                BigDecimal noZeros = value.stripTrailingZeros();
                String data = noZeros.toPlainString();
                String[] strs = data.split("\\.");
                if (strs.length > 1 && strs[1].length() > excelCheck.decimal()) {
                    StringBuilder err = new StringBuilder("第").append(i).append("行，").append(fieldName).append("小数长度超过").append(excelCheck.decimal()).append("位；");
                    errList.add(err.toString());
                }
            } else {
                StringBuilder err = new StringBuilder("第").append(i).append("行，").append(fieldName).append("不是数值；");
                errList.add(err.toString());
            }
        }

    }

    /**
     * 整数长度
     * @param v 值
     * @param excelCheck 注解
     * @param i 行数
     * @param fieldName excel段名
     * @param errList 错误信息集合
     */
    private static void integer(Object v, ExcelCheck excelCheck, int i, String fieldName, List<String> errList) {
        if (excelCheck.integer() != -1 && ObjectUtil.isNotEmpty(v)) {
            if (isNumeric(v.toString())) {
                String[] strs = v.toString().split("\\.");
                if (strs[0].length() > excelCheck.integer()) {
                    StringBuilder err = new StringBuilder("第").append(i).append("行，").append(fieldName).append("整数长度超过").append(excelCheck.integer()).append("位；");
                    errList.add(err.toString());
                }
            } else {
                StringBuilder err = new StringBuilder("第").append(i).append("行，").append(fieldName).append("不是数值；");
                errList.add(err.toString());
            }
        }
    }

    /**
     * 不能为空
     * @param v 值
     * @param excelCheck 注解
     * @param i 行数
     * @param fieldName excel段名
     * @param errList 错误信息集合
     */
    public static void isNotNull(Object v, ExcelCheck excelCheck, int i, String fieldName, List<String> errList) {
        if (excelCheck.isNotNull() && ObjectUtil.isEmpty(v)) {
            StringBuilder err = new StringBuilder("第").append(i).append("行，").append(fieldName).append("不能为空；");
            errList.add(err.toString());
        }
    }

    /**
     * excel 对象数据校验
     * @param excelList
     * @param clazz
     * @return
     */
    public static List<String> excelCheck(List<?> excelList, Class<?> clazz) {
        List<String> errList = excelCheckObj(excelList, clazz);
        return errList;
    }


    /**
     * 判断是否为数值
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        return str != null && NUMBER_PATTERN.matcher(str).matches();
    }

    /**
     * 时间是否合格
     * @param strDate 时间
     * @param dateFormat "yyyy-MM-dd"
     * @param legalLen 10 , 19
     * @return
     */
    private static boolean isDate(String strDate, String dateFormat, int legalLen) {
        if ((strDate == null) || (strDate.length() != legalLen)) {
            return false;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            Date date = formatter.parse(strDate);
            return strDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }



}


