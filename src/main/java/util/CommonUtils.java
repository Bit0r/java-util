package util;



import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 通用相关 工具类
 * </p>
 *
 * @author zhichong
 * @since 2020-06-10
 */
public class CommonUtils {

    private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

//    /**
//     * 模糊查询
//     *
//     * @param queryWrapper
//     * @param blurryMap
//     * @param object
//     * @return
//     */
//    private static QueryWrapper<Object> getBlurryMap(QueryWrapper<Object> queryWrapper, Map<String, Object> blurryMap, Object object) {
//        if (blurryMap == null || blurryMap.isEmpty()) {
//            return queryWrapper;
//        }
//        Iterator<Map.Entry<String, Object>> iterator = blurryMap.entrySet().iterator();
//        Set<String> keySet = new HashSet<>();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Object> entry = iterator.next();
//            String key = entry.getKey();
//            String value = String.valueOf(entry.getValue());
//            if (StringUtils.isNotEmpty(value)) {
//                keySet.add(key);
//            }
//        }
//        HashMap<String, Object> hashMap = getProxyPojoValue(object, keySet);
//        for (String data : keySet) {
//            String value = (String) blurryMap.get(data);
//            String fieldName = (String) hashMap.get(data);
//            if (StringUtils.isEmpty(fieldName)) {
//                fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, data);
//            }
//            queryWrapper.like(fieldName, value);
//        }
//        return queryWrapper;
//    }
//
//    /**
//     * 精确查询
//     *
//     * @param queryWrapper
//     * @param accurateMap
//     * @param object
//     * @return
//     */
//    private static QueryWrapper<Object> getAccurateMap(QueryWrapper<Object> queryWrapper, Map<String, Object> accurateMap, Object object) {
//        if (accurateMap == null || accurateMap.isEmpty()) {
//            return queryWrapper;
//        }
//        Iterator<Map.Entry<String, Object>> iterator = accurateMap.entrySet().iterator();
//        Set<String> keySet = new HashSet<>();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Object> entry = iterator.next();
//            String key = entry.getKey();
//            String value = String.valueOf(entry.getValue());
//            if (StringUtils.isNotEmpty(value)) {
//                keySet.add(key);
//            }
//        }
//        HashMap<String, Object> hashMap = getProxyPojoValue(object, keySet);
//        for (String data : keySet) {
//            String value = (String) accurateMap.get(data);
//            String fieldName = (String) hashMap.get(data);
//            if (StringUtils.isEmpty(fieldName)) {
//                fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, data);
//            }
//            queryWrapper.eq(fieldName, value);
//        }
//        return queryWrapper;
//    }
//
//    /**
//     * 时间查询
//     *
//     * @param queryWrapper
//     * @param timeMap
//     * @param object
//     * @return
//     */
//    private static QueryWrapper<Object> getTimeMap(QueryWrapper<Object> queryWrapper, Map<String, Object> timeMap, Object object) {
//        if (timeMap == null || timeMap.isEmpty()) {
//            return queryWrapper;
//        }
//        Iterator<Map.Entry<String, Object>> iterator = timeMap.entrySet().iterator();
//        Set<String> keySet = new HashSet<>();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Object> entry = iterator.next();
//            String key = entry.getKey();
//            key = key.substring(0, key.indexOf("|"));
//            String value = String.valueOf(entry.getValue());
//            if (StringUtils.isNotEmpty(value)) {
//                keySet.add(key);
//            }
//        }
//        HashMap<String, Object> hashMap = getProxyPojoValue(object, keySet);
//        for (String data : keySet) {
//            String startTime = (String) timeMap.get(data + "|startTime");
//            String endTime = (String) timeMap.get(data + "|endTime");
//            String fieldName = (String) hashMap.get(data);
//            if (StringUtils.isEmpty(fieldName)) {
//                fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, data);
//            }
//            if (StringUtils.isNotEmpty(startTime)) {
//                queryWrapper.ge(fieldName, startTime);
//            }
//            if (StringUtils.isNotEmpty(endTime)) {
//                queryWrapper.le(fieldName, endTime);
//            }
//        }
//        return queryWrapper;
//    }
//
//    /**
//     * 拼接sql语句
//     *
//     * @param query
//     * @return QueryWrapper<Object>
//     */
//    public static Object getQueryMapSql(CriteriaQuery query, Object object) {
//        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
//        if (query == null) {
//            return queryWrapper;
//        }
//        Map<String, Object> blurryMap = query.getBlurryMap();
//        if (blurryMap != null) {
//            queryWrapper = getBlurryMap(queryWrapper, blurryMap, object);
//        }
//        Map<String, Object> accurateMap = query.getAccurateMap();
//        if (accurateMap != null) {
//            queryWrapper = getAccurateMap(queryWrapper, accurateMap, object);
//        }
//        Map<String, Object> timeMap = query.getTimeMap();
//        if (timeMap != null) {
//            queryWrapper = getTimeMap(queryWrapper, timeMap, object);
//        }
//        return queryWrapper;
//    }


    /**
     * 获取实体类的TableName和TableField
     *
     * @param object
     * @param key1
     * @return
     */
    public static HashMap<String, Object> getProxyPojoValue(Object object, Set<String> key1) {
        String id = null;
        // 返回参数
        HashMap<String, Object> hashMap = new HashMap<>(16);
        for (String s : key1) {
            List<Field> fieldList = new ArrayList<>();
            Class tempClass = object.getClass();
            while (tempClass != null) {
                //当父类为null的时候说明到达了最上层的父类(Object类).
                fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
                //得到父类,然后赋给自己
                tempClass = tempClass.getSuperclass();
            }
            for (Field field : fieldList) {
                field.setAccessible(true);

                // 获取表名
                TableName table = object.getClass().getAnnotation(TableName.class);
                if (table != null) {
                    String tableName = table.value();
                    hashMap.putIfAbsent("tableName", tableName);
                }
                // 获取主键id
                if (id == null) {
                    boolean isIdField = field.isAnnotationPresent(TableId.class);
                    if (isIdField) {
                        TableField tableField = field.getAnnotation(TableField.class);
                        if (s.toLowerCase().equals(field.getName().toLowerCase())) {
                            String tableId = tableField.value();
                            hashMap.put(s, tableId);
                            id = tableId;
                        }
                    }
                }

                // 获取字段的值
                boolean isTableField = field.isAnnotationPresent(TableField.class);
                if (isTableField) {
                    TableField tableField = field.getAnnotation(TableField.class);
                    if (s.toLowerCase().equals(field.getName().toLowerCase())) {
                        String fieldValue = tableField.value();
                        hashMap.put(s, fieldValue);
                    }
                }
            }
        }
        System.out.println(hashMap);
        return hashMap;
    }


    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 获取错误提示
     * 实体中的@NotBlank @NotNull
     *
     * @param fieldErrorList
     * @param map            map.put("字段名","提示语");
     *                       提示是否替换 map.put("bigType","大类不能为空ZC"); 不替换默认使用注解中的message值
     * @return
     */
    public static String getEntityMessage(List<FieldError> fieldErrorList, Map<String, Object> map) {
        if (map.isEmpty()) {
            return getEntityMessage(fieldErrorList);
        }
        Map<String, Object> messageMap = new HashMap<>();
        fieldErrorList.forEach(data -> {
            if (data.getCodes() != null && data.getCodes().length > 0) {
                String field = data.getField();
                String message = data.getDefaultMessage();
                if (map.containsKey(field)) {
                    message = (String) map.get(field);
                }
                messageMap.put(field, message);
            }
        });
        JSONObject json = new JSONObject(messageMap);
        return json.toString();
    }

    /**
     * 获取错误提示
     * 实体中的@NotBlank @NotNull
     * 提示默认使用@NotNull中的message值
     *
     * @param objectErrorList
     * @return
     */
    public static String getEntityMessage(List<FieldError> objectErrorList) {
        Map<String, Object> messageMap = new HashMap<>();
        objectErrorList.forEach(data -> {
            if (data.getCodes() != null && data.getCodes().length > 0) {
                String field = data.getField();
                String message = data.getDefaultMessage();
                messageMap.put(field, message);
            }
        });
        JSONObject json = new JSONObject(messageMap);
        return json.toString();
    }

    /**
     * 将对象属性转化为map结合
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map集合中的数据转化为指定对象的同名属性中
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) throws Exception {
        T bean = clazz.newInstance();
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 如果返回null则表示没有错误
     * 通过实体中注解 必填校验
     *
     * @param objectList
     * @return
     */
    public static String check(List objectList) {
        if (null == objectList || objectList.size() < 1) {
            return "参数为空";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objectList.size(); i++) {
            Object date = objectList.get(i);
            String res = check(date);
            if (res != null) {
                sb.append("第" + i + "列数据：").append(res).append(".");
            }
        }
        if (sb.length() > 0 && !"null".equals(sb.toString()) && !"".equals(sb.toString())) {
            return sb.toString();
        }
        return null;
    }

    /**
     * 如果返回null则表示没有错误
     * 通过实体中注解 必填校验
     *
     * @param obj
     * @return
     */
    public static String check(Object obj) {
        if (null == obj) {
            return "参数为空";
        }
        Set<ConstraintViolation<Object>> validResult = Validation.buildDefaultValidatorFactory().getValidator().validate(obj);
        if (null != validResult && validResult.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Iterator<ConstraintViolation<Object>> iterator = validResult.iterator(); iterator.hasNext(); ) {
                ConstraintViolation<Object> constraintViolation = (ConstraintViolation<Object>) iterator.next();
                if (StringUtils.isNotEmpty(constraintViolation.getMessage())) {
                    sb.append(constraintViolation.getMessage()).append("、");
                } else {
                    sb.append(constraintViolation.getPropertyPath().toString()).append("不合法");
                }
            }
            if (sb.lastIndexOf("、") == sb.length() - 1) {
                sb.delete(sb.length() - 1, sb.length());
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 字符串转list（从右到左截取）
     *
     * @param str    字符串
     * @param length 每次截取长度（List中的字符串长度）
     * @return
     */
    public static List<String> getStringToList(String str, Integer length) {
        List<String> list = new ArrayList<>();
        while (str.length() > length) {
            list.add(str);
            str = str.substring(0, str.length() - 2);
        }
        list.add(str);
        return list;
    }

//    /**
//     * 获取枚举 FieldName注解值
//     *
//     * @param enumClass
//     * @param string
//     * @return
//     */
//    public static String getEnumFieldName(Class enumClass, String string) {
//        String enumFieldName = "";
//        try {
//            FieldName fieldName = enumClass
//                    .getField(string)
//                    .getAnnotation(FieldName.class);
//            enumFieldName = fieldName.value();
//        } catch (NoSuchFieldException e) {
//            logger.error(e.getMessage());
//        }
//        return enumFieldName;
//    }
//
//    /**
//     * 获取枚举 FieldName注解值
//     *
//     * @param enumClass
//     * @param string
//     * @return
//     */
//    public static String getEnumFieldNames(Class enumClass, String... strings) {
//        if(strings==null||strings.length==0) {
//            return "";
//        }
//        StringBuilder enumFieldNames = new StringBuilder();
//        for(String string:strings) {
//            try {
//                FieldName fieldName = enumClass
//                        .getField(string)
//                        .getAnnotation(FieldName.class);
//                enumFieldNames.append(fieldName.value()).append(",");
//            } catch (NoSuchFieldException e) {
//                logger.error(e.getMessage());
//            }
//        }
//        int length=enumFieldNames.length();
//        if(length>1) {
//            enumFieldNames.deleteCharAt(length-1);
//        }
//        return enumFieldNames.toString();
//    }
//
//    /**
//     * 根据中文获取枚举值
//     *
//     * @param enumClass
//     * @param string
//     * @return
//     */
//    public static String getEnumByFieldName(Class enumClass, String string) {
//        String enumStringName = "";
//        try {
//            for (Field field : enumClass.getFields()) {
//                FieldName fieldName = field.getAnnotation(FieldName.class);
//                String enumFieldName = fieldName.value();
//                if (string.equalsIgnoreCase(enumFieldName)) {
//                    enumStringName = field.getName();
//                }
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//        return enumStringName;
//    }

    /**
     * 复制实体类
     *
     * @param source 被复制的实体类对象
     * @param to     复制完后的实体类对象
     * @throws Exception
     */
    public static void copy(Object source, Object to, String... needProperties) throws Exception {
        List<String> propertiesList = new ArrayList(Arrays.asList(needProperties));
        // 获取属性
//        BeanInfo sourceBean = Introspector.getBeanInfo(source.getClass(), java.lang.Object.class);
//        PropertyDescriptor[] sourceProperty = sourceBean.getPropertyDescriptors();

        BeanInfo destBean = Introspector.getBeanInfo(to.getClass(), java.lang.Object.class);
        PropertyDescriptor[] destProperty = destBean.getPropertyDescriptors();

        //不复制的字段
        Set<String> notCopyFieldList = new HashSet<>();
        for (int j = 0; j < destProperty.length; j++) {
            if (!propertiesList.contains(destProperty[j].getName())) {
                notCopyFieldList.add(destProperty[j].getName());
            }
        }
        if (notCopyFieldList != null && notCopyFieldList.size() > 0) {
            String[] strs1 = notCopyFieldList.toArray(new String[notCopyFieldList.size()]);
            BeanUtils.copyProperties(source, to, strs1);
        }
    }

    /**
     * 正则表达式验证
     *
     * @param regex 正则表达式
     * @param str   需要验证的字符串
     * @return
     */
    public static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    /**
     * 获取BigDecimal 有几位小数
     *
     * @param bigDecimal
     * @return
     */
    public static Integer getDecimalLength(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return 0;
        }
        String s = bigDecimal.toPlainString();
        String[] st = s.split("\\.");
        if (st.length < 2) {
            return 0;
        }
        return st[1].length();
    }


    /**
     * 去除字符串后面的0 零
     *
     * @param data
     * @return
     */
    public static String getRemoveZeroString(String data) {
        return data.replaceAll("0+$", "");
    }


    /**
     * 多个list取交集
     * @param lists
     * @return
     */
    public static List<String> getListIntersection(List... lists){
        if(ObjectUtils.isEmpty(lists)){
            return new ArrayList();
        }
        List<List> dataList = new ArrayList(Arrays.asList(lists));
        List<String> intersectionList  = dataList.get(0);
        for (List data : dataList){
            if(ObjectUtils.isEmpty(data)){
                return new ArrayList();
            }
            intersectionList = intersectionList.stream().filter(item -> data.contains(item)).collect(toList());
        }
        return intersectionList;
    }

    /**
     * 获取服务器ip
     * @return
     */
    public static String getIp(){
        InetAddress address = null;
        String ip = "";
        try {
            address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 根据身份证号获取年龄
     * @param idNumber
     * @return
     */
    public static int getAgeByIDNumber(String idNumber){
        int invalidAge = -1;
        String dateStr;
        if (idNumber.length() == 15) {
            dateStr = "19" + idNumber.substring(6, 12);
        } else if (idNumber.length() == 18) {
            dateStr = idNumber.substring(6, 14);
        } else {//默认是合法身份证号，但不排除有意外发生
            return invalidAge;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date birthday = simpleDateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            if (calendar.getTimeInMillis() - birthday.getTime() < 0L) {
                return invalidAge;
            }
            int yearNow = calendar.get(Calendar.YEAR);
            int monthNow = calendar.get(Calendar.MONTH);
            int dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTime(birthday);
            int yearBirthday = calendar.get(Calendar.YEAR);
            int monthBirthday = calendar.get(Calendar.MONTH);
            int dayOfMonthBirthday = calendar.get(Calendar.DAY_OF_MONTH);

            int age = yearNow - yearBirthday;


            if (monthNow <= monthBirthday && monthNow == monthBirthday && dayOfMonthNow < dayOfMonthBirthday || monthNow < monthBirthday) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            return invalidAge;
        }
    }

    /**
     * in 条件
     * @param mapList
     * @return
     */
    public static String getInCondition(List<String> mapList){
        if(org.apache.commons.lang3.ObjectUtils.isNotEmpty(mapList)){
            StringBuilder sbatchIds = new StringBuilder(" (");
            for(int i =0 ; i<mapList.size() ;i++){
                if(i==0){
                    sbatchIds.append(" '"+mapList.get(i)+"'");
                }else {
                    sbatchIds.append(",'"+mapList.get(i)+"'");
                }
            }
            sbatchIds.append(" )");
            return  sbatchIds.toString();
        }
        return null;
    }

    /**
     * 剔除original 中的 eliminate元素
     * @param original 逗号隔开的 源数据
     * @param eliminate 逗号隔开的 剔除数据
     * @return
     */
    public static String getStringEliminate(String original ,String eliminate){
        if(StringUtils.isEmpty(original)){
            return null;
        }
        if(StringUtils.isEmpty(eliminate)){
            return original;
        }
        List<String> originalList = Arrays.asList(original.split(","));
        List<String> eliminateList = Arrays.asList(eliminate.split(","));
        List<String> list =originalList.stream().filter(item -> !eliminateList.contains(item)).collect(toList());
        return String.join(",", list);
    }

    public static List<String> fsToList(String receiveId){
        return Stream.of(receiveId.split("\\|")).collect(Collectors.toList());
    }

    /**
     * list获取不重复的元素字段
     * @param list 源数据
     * @param classifier 去重字段
     * @return 去重字段set
     */
    public static <T, S> Set<T> distinctListParam(List<S> list,Function<? super S, ? extends T> classifier){

        Set<T> set = new LinkedHashSet<T>();
        for(S s:list) {
            set.add(classifier.apply(s));
        }

        return set;
    }
}


