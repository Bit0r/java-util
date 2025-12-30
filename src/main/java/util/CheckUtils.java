package util;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 字段校验工具类
 */
public class CheckUtils {

    private static String SEPARATOR = ".";
    private static String SYSTEM = "trade";
    private static String SUB_OBJECT = "-SUBobject";
    private static String LIST = "java.util.List";
    private static Logger log = LoggerFactory.getLogger(CheckUtils.class);

    /**
     * 获取字段和注解相关信息
     * @param objectClazz
     * @param annotationClass
     * @return k-字段名称 v-注解信息
     */
    private static Map<String, Object> getNotNullInfo(Class<?> objectClazz, Class annotationClass,Set<String> classNameSet, Map<String, Object> getAnnotateInfoMap) {
        if(classNameSet == null){
            classNameSet = new HashSet<>();
        }
        if(getAnnotateInfoMap == null){
            getAnnotateInfoMap = new HashMap<>();
        }

        String className = objectClazz.getName();
        if(classNameSet.contains(className)){
            return getAnnotateInfoMap;
        }
        classNameSet.add(className);
//        Map<String, Object> getAnnotateInfoMap = new HashMap<>();

        List<Field> fieldList = TableInfoHelper.getAllFields(objectClazz);

        for (Field field : fieldList) {
            Object annotate = field.getAnnotation(annotationClass);
            if (annotate != null) {
                getAnnotateInfoMap.put(className +SEPARATOR+ field.getName(), annotate);
            }
            if (List.class.equals(field.getType())) {
                Type type = field.getGenericType();
                if (type != null) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) type;
                        Class sonObjectClass = (Class) pt.getActualTypeArguments()[0];
                        Map<String, Object> sonObjectAnnotateInfoMap = getNotNullInfo(sonObjectClass, annotationClass, classNameSet,getAnnotateInfoMap);

//                        getAnnotateInfoMap.put(prefix + field.getName() + SUB_OBJECT, sonObjectAnnotateInfoMap);
                    }
                }
            } else if (field.getType().toString().contains(SYSTEM)) {
                Map<String, Object> sonObjectAnnotateInfoMap = getNotNullInfo(field.getType(), annotationClass, classNameSet,getAnnotateInfoMap);
//                getAnnotateInfoMap.put(prefix + field.getName() + SUB_OBJECT, sonObjectAnnotateInfoMap);
            }


        }
        return getAnnotateInfoMap;
    }

    /**
     * 获取错误信息
     * @param objList
     * @return
     */
    private static List<String> getErrList(List<?> objList, Map<String, Object> notNullInfoMap,  List<String> errList) {
        for (int i = 0; i < objList.size(); i++) {
            Object obj = objList.get(i);
            String className = obj.getClass().getName();
            PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(obj);
            for (int j = 0; j < origDescriptors.length; j++) {
                String k = origDescriptors[j].getName();
                Object v = null;
                try {
                    v = PropertyUtils.getSimpleProperty(obj, k);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                Object notNullObject = notNullInfoMap.get(className+ SEPARATOR + k);
                if (ObjectUtil.isNotEmpty(notNullObject)) {
                    if (ObjectUtil.isEmpty(v)) {
                        setErrList(errList, className+ SEPARATOR + k, i, objList.size());
                    }
                }
                if (ObjectUtil.isNotEmpty(v)) {
                    if (v instanceof List) {
                        List list = (List) v;
                        if(list!= null && list.get(0).getClass().getName().contains(SYSTEM)){
                            getErrList((List<?>) v, notNullInfoMap, errList);
                        }
                    } else {
                        if(v.getClass().getName().contains(SYSTEM)){
                            getErrList(Collections.singletonList(v), notNullInfoMap,  errList);
                        }

                    }
//                    Object subObjectObject = notNullInfoMap.get(prefix + k + SUB_OBJECT);
//                    if (ObjectUtil.isNotEmpty(subObjectObject)) {
//                        if (v instanceof List) {
//
//                            getErrList((List<?>) v, (Map<String, Object>) subObjectObject, prefix + k, errList);
//                        } else {
//                            getErrList(Collections.singletonList(v), (Map<String, Object>) subObjectObject, prefix + k, errList);
//                        }
//                    }
                }
            }
        }
        return errList;
    }

    /**
     * 不为空判断-获取错误信息
     * @param objList
     * @return
     */
    public static List<String> getErrList(List<?> objList) {
        List<String> errList = new ArrayList<>();
        if (CollUtil.isEmpty(objList)) {
            return errList;
        }
        Map<String, Object> notNullInfoMap = getNotNullInfo(objList.get(0).getClass(), NotNull.class, new HashSet<>(),null);
        getErrList(objList, notNullInfoMap, errList);
        return errList;
    }

    /**
     * 错误信息
     * @param errList
     * @param k
     * @param i
     * @param size
     */
    private static void setErrList(List<String> errList, String k, int i, int size) {
        StringBuilder err = new StringBuilder();
        if (size > 1) {
            err = err.append("第").append(i).append("行，").append(k).append("不能为空；");
        } else {
            err = err.append(k).append("不能为空；");
        }

//        err = err.append("第").append(i).append("行，").append(k).append("不能为空；");
        errList.add(err.toString());
    }





}


