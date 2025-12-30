//package util;
//
//
//
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.EasyExcelFactory;
//import com.alibaba.excel.ExcelReader;
//import com.alibaba.excel.ExcelWriter;
//import com.alibaba.excel.annotation.ExcelProperty;
//import com.alibaba.excel.read.metadata.ReadSheet;
//import com.alibaba.excel.write.handler.WriteHandler;
//import com.alibaba.excel.write.metadata.WriteSheet;
//import com.alibaba.excel.write.metadata.fill.FillConfig;
//import com.alibaba.excel.write.metadata.style.WriteCellStyle;
//import com.alibaba.excel.write.metadata.style.WriteFont;
//import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
//import com.github.pagehelper.util.StringUtil;
//import org.apache.commons.beanutils.PropertyUtils;
//import org.apache.poi.ss.formula.functions.T;
//import org.apache.poi.ss.usermodel.BorderStyle;
//import org.apache.poi.ss.usermodel.IndexedColors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletResponse;
//import java.beans.PropertyDescriptor;
//import java.io.*;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.math.BigDecimal;
//import java.net.URLEncoder;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//
///**
// * EasyExcel 工具类
// * @author zc
// */
//public class EasyExcelUtils {
//
//    private EasyExcelUtils() {
//        throw new BaseException(Status.UNEXCEPTEDERROR);
//    }
//
//    /**
//     * 导入限制最多一次导入5000条记录
//     */
//    private static final Integer IMPORT_SUM = 5000;
//
//    /**
//     * 导出限制最多一次导入10000条记录
//     */
//    public static final Integer EXPORT_SUM = 10000;
//
//    /**
//     * BigDecimal 去除尾数的0
//     */
//    private static final String BIGDECIMAL = "class java.math.BigDecimal";
//
//    /**
//     * DATE 时间转换
//     */
//    private static final String DATE = "class java.util.Date";
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(EasyExcelUtils.class);
//
//    public static <T> List<T> read(String filePath, final Class<?> clazz) {
//        File f = new File(filePath);
//        try (FileInputStream fis = new FileInputStream(f)) {
//            return read(fis, clazz);
//        } catch (FileNotFoundException e) {
//            LOGGER.error("文件{}不存在", filePath, e);
//        } catch (IOException e) {
//            LOGGER.error("文件读取出错", e);
//        }
//        return new ArrayList<>();
//    }
//
//    /**
//     * 解析多个Sheet 的excel数据
//     * @param file 文件
//     * @param clazzMap  k:第几个页签 v:页签对应的excel实体类 如： clazzMap.put(0, BillingStandardVersionExcel.class); 从0开始
//     * @return Map<Integer, Object> k:第几个页签 v：页签数据
//     * @throws IOException
//     */
//    public static Map<Integer, Object> readList(MultipartFile file, Map<Integer, Class<?>> clazzMap) throws IOException {
//        Map<Integer, Object> listMap = new HashMap<>();
//        if (file == null) {
//            throw new BaseException(Status.FAILURE, "解析出错了，文件流是null");
//        }
//        if (ObjectUtil.isEmpty(clazzMap)) {
//            throw new BaseException(Status.FAILURE, "解析出错了，参数为空");
//        }
//        ExcelReader excelReaderOnePart = EasyExcelFactory.read(file.getInputStream()).build();
//        List<ReadSheet> readSheets = EasyExcelFactory.read(file.getInputStream()).build().excelExecutor().sheetList();
//        if (!readSheets.isEmpty()) {
//            for (Map.Entry<Integer, Class<?>> data : clazzMap.entrySet()) {
//                Integer key = data.getKey();
//                Class<?> clazz = data.getValue();
//                ReadSheet readSheet = readSheets.get(key);
//                DataListener<Object> titleListener = new DataListener<>();
//
//                if (clazz.equals(Map.class)) {
//                    //转化为 List<List<String>> 数据，从第一行开始
//                    List<List<String>> lists = new ArrayList<>();
//                    readSheet = EasyExcelFactory.readSheet(readSheet.getSheetNo()).head(lists).registerReadListener(titleListener).headRowNumber(0).build();
//                } else {
//                    //从第一行开始读
//                    readSheet = EasyExcelFactory.readSheet(readSheet.getSheetNo()).head(clazz).registerReadListener(titleListener).headRowNumber(1).build();
//                }
//                excelReaderOnePart.read(readSheet);
//                if (titleListener.getRows().size() > IMPORT_SUM) {
//                    throw new Exception(Status.FAILURE, "导入数据过多，一次最多导入" + IMPORT_SUM + "条数据");
//                }
////                listMap.put(clazz.getName(), titleListener.getRows());
//                listMap.put(key, titleListener.getRows());
//            }
//
//        }
//        return listMap;
//    }
//
//    /**
//     * excel 读取
//     * @param inputStream 文件流
//     * @param clazz 实体
//     * @param <T>
//     * @return
//     */
//    public static <T> List<T> read(InputStream inputStream, final Class<?> clazz) {
//        if (inputStream == null) {
//            throw new BaseException(Status.FAILURE, "解析出错了，文件流是null");
//        }
//        // 有个很重要的点 DataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
//        DataListener<T> listener = new DataListener<>();
//        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
//        EasyExcelFactory.read(inputStream, clazz, listener).sheet().doRead();
//        //导入模板表头校验
//        headCheck(listener.headNameMap, clazz);
//        if (listener.getRows().size() > IMPORT_SUM) {
//            throw new BaseException(Status.FAILURE, "导入数据过多，一次最多导入" + IMPORT_SUM + "条数据");
//        }
//        return listener.getRows();
//    }
//
//    /**
//     * 导入模板表头校验
//     * @param headNameMap
//     * @param clazz
//     */
//    private static void headCheck(Map<Integer, String> headNameMap, Class<?> clazz) {
//        List<Field> fieldList = TableInfoHelper.getAllFields(clazz);
//        int j = 0;
//        for (int i = 0; i < fieldList.size(); i++) {
//            ExcelProperty excelProperty = fieldList.get(i).getAnnotation(ExcelProperty.class);
//            if (ObjectUtil.isNotEmpty(excelProperty)) {
//                String headName = headNameMap.get(j);
//                j++;
//                //TODO 这里只用了一个字段的 多个字段还需要后期测试修改
//                if (!excelProperty.value()[0].equals(headName)) {
//                    throw new BaseException(Status.FAILURE, "请使用导入模板导入");
//                }
//            }
//        }
//    }
//
//
//    public static void write(String outFile, List<?> list) {
//        Class<?> clazz = list.get(0).getClass();
//        EasyExcelFactory.write(outFile, clazz).sheet().doWrite(list);
//    }
//
//    public static void write(String outFile, List<?> list, String sheetName) {
//        Class<?> clazz = list.get(0).getClass();
//        EasyExcelFactory.write(outFile, clazz).sheet(sheetName).doWrite(list);
//    }
//
//    public static void write(OutputStream outputStream, List<?> list, String sheetName) {
//        Class<?> clazz = list.get(0).getClass();
//        // sheetName为sheet的名字，默认写第一个sheet
//        EasyExcelFactory.write(outputStream, clazz).sheet(sheetName).doWrite(list);
//    }
//
//    private static WriteHandler templateWriteHandler;
//    static {
//        //表头样式
//        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
//        //字体
//        WriteFont headWriteFont = new WriteFont();
//        headWriteFont.setFontHeightInPoints((short) 11);
//        headWriteFont.setBold(true);
//        headWriteCellStyle.setWriteFont(headWriteFont);
//        //边框
//        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
//        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
//        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
//        //前景色
//        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
//        //是否换行
//        headWriteCellStyle.setWrapped(true);
//        headWriteCellStyle.setLocked(true);
//        //表体样式
//        WriteCellStyle bodyWriteCellStyle = new WriteCellStyle();
//        //设置数据格式索引
////        bodyWriteCellStyle.setDataFormat((short)49);
//
//        templateWriteHandler = new HorizontalCellStyleStrategy(headWriteCellStyle,bodyWriteCellStyle);
//    }
//
//
//    /**
//     * 文件下载（失败了会返回一个有部分数据的Excel），用于直接把excel返回到浏览器下载
//     * @param response
//     * @param list
//     * @param sheetName
//     */
//    public static void download(HttpServletResponse response, List<?> list, String sheetName) {
//        if (list.size() > EXPORT_SUM) {
//            throw new BaseException(Status.FAILURE, "导出数据过多，一次最多导出" + EXPORT_SUM + "条数据");
//        }
//        String fileName = null;
//        try {
//            //数据特殊处理
//            try {
//                stripTrailingZeros(list);
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            Class<?> clazz = list.get(0).getClass();
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setCharacterEncoding("utf-8");
//            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
//            fileName = URLEncoder.encode(sheetName, "UTF-8").replace("\\+", "%20");
//            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
//            EasyExcelFactory.write(response.getOutputStream(), clazz).head(clazz).sheet(sheetName)
////                    .registerWriteHandler(new TextCellWriteHandlerImpl())
////                    .registerWriteHandler(new TextCellWriteHandlerImpl())
//                    .doWrite(list);
//        } catch (IOException e) {
//            LOGGER.error("download", e);
//            throw new BaseException(Status.FAILURE, "导出失败");
//        }
//
//    }
//
//    /**
//     * 多个sheet文件下载
//     * @param response
//     * @param excelMap
//     * @param excelTableNameMap
//     */
//    public static void download(HttpServletResponse response, Map<Integer,Object> excelMap,  Map<Integer,String> excelTableNameMap,String fileName) {
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
//        try {
//            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
//            //新建ExcelWriter
//            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
//            for (Map.Entry<Integer,Object> entry : excelMap.entrySet()){
//                Integer k = entry.getKey();
//                List v = (List) entry.getValue();
//                String sheetName = excelTableNameMap.get(k);
//                if(sheetName == null){
//                    sheetName = "sheet"+k;
//                }
//                Class<?> clazz = v.get(0).getClass();
//                //获取sheet0对象
//                WriteSheet mainSheet = EasyExcel.writerSheet(k, sheetName).head(clazz).build();
//                excelWriter.write(v, mainSheet);
//            }
//            //关闭流
//            excelWriter.finish();
//        } catch (IOException e) {
//            LOGGER.error("download", e);
//            throw new BaseException(Status.FAILURE, "导出失败");
//        }
//    }
//
////    public static void main(String[] args) {
////        CopperyMineDto dto = new CopperyMineDto();
//////			dto.setOrderDate(new Date());
////        dto.setPaymentDate(new Date());
////
////        CopperyMineExcel excel = new CopperyMineExcel();
////        BeanUtil.copyProperties(dto, excel);
////        Class<?> clazz = CopperyMineExcel.class;
////        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(excel);
//////        Class<?> clazz = origDescriptors[0].getPropertyType();
////        List<String> dateNameList = new ArrayList<>();
////        for (Field field : TableInfoHelper.getAllFields(clazz)) {
////            if(field.getName().equals("materialName")){
////
////                System.out.println("materialName");
////            }
////            ExcelCheck excelCheck = field.getAnnotation(ExcelCheck.class);
////            if(excelCheck != null && ObjectUtil.isNotEmpty(excelCheck.dateFormat())){
////                dateNameList.add(field.getName());
////            }
////        }
////        System.out.println(JSON.toJSONString(dateNameList));
////
////    }
//
//    /**
//     * 数据特殊处理
//     * @param list
//     */
//    public static void stripTrailingZeros(List<?> list) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ParseException {
//        if(CollUtil.isEmpty(list)){
//            return;
//        }
//
//        Map<String,String> dateNameMap = new HashMap<>();
//        Class<?> clazz = list.get(0).getClass();
//        for (Field field : TableInfoHelper.getAllFields(clazz)) {
//            ExcelCheck excelCheck = field.getAnnotation(ExcelCheck.class);
//            if(excelCheck != null && ObjectUtil.isNotEmpty(excelCheck.dateFormat())){
//                dateNameMap.put(field.getName(),excelCheck.dateFormat());
//            }
//        }
//
//        List<String> bigDecimalNameList = new ArrayList<>();
//        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(list.get(0));
//        for (int i = 0; i < origDescriptors.length; i++) {
//            String type = origDescriptors[i].getPropertyType().toString();
//            if(BIGDECIMAL.equals(type)){
//                String name = origDescriptors[i].getName();
//                bigDecimalNameList.add(name);
//            }
//        }
//        for (Object data: list){
//            //时间转换
//            if(CollUtil.isNotEmpty(dateNameMap)){
//                for (Map.Entry<String,String> entry : dateNameMap.entrySet()){
//                    String name = entry.getKey();
//                    String dateFormat = entry.getValue();
//                    String value = (String) PropertyUtils.getSimpleProperty(data, name);
//                    if(value != null && StringUtil.isNotEmpty(dateFormat)){
//                        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
//                        Date date = sdf1.parse(value);
//                        //日期转字符串
//                        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormat);
//                        String formatStr = sdf2.format(date);
//                        PropertyUtils.setSimpleProperty(data, name,formatStr);
//                    }
//                }
//            }
//            //去除尾部0
//            if (CollUtil.isNotEmpty(bigDecimalNameList)){
//                for (String name : bigDecimalNameList){
//                    BigDecimal value = (BigDecimal) PropertyUtils.getSimpleProperty(data, name);
//                    if(value != null){
//                        PropertyUtils.setSimpleProperty(data, name,value.stripTrailingZeros());
//                    }
//
//                }
//            }
//        }
//    }
//
//
//    /**
//     * excel 填充
//     * @param response
//     * @param list
//     * @param obj
//     * @param templateUrl
//     * @param sheetName
//     */
//    public static void excelFill(HttpServletResponse response, List list,Object obj,String templateUrl, String sheetName) {
//
//        //使用模板
//        //获取模板
//        ClassPathResource classPathResource = new ClassPathResource(templateUrl);
//        //输入流
//        InputStream inputStream = null;
//        //输出流
//        ServletOutputStream outputStream = null;
//        //Excel对象
//        ExcelWriter excelWriter = null;
//        try {
//            //输入流
//            inputStream = classPathResource.getInputStream();
//            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
//            response.setContentType("application/vnd.ms-excel");
//            response.setCharacterEncoding("utf-8");
//            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
//            String fileName = URLEncoder.encode(sheetName, "UTF-8");
//            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
//            outputStream = response.getOutputStream();
//
//            //设置输出流和模板信息
//            excelWriter = EasyExcel.write(outputStream).withTemplate(inputStream).build();
//            WriteSheet writeSheet = EasyExcel.writerSheet().build();
//            //开启自动换行,自动换行表示每次写入一条list数据是都会重新生成一行空行,此选项默认是关闭的,需要提前设置为true
//            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
//            //列表
//            excelWriter.fill(list, fillConfig, writeSheet);
//            //对象
//            excelWriter.fill(obj, writeSheet);
//            excelWriter.finish();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // 千万别忘记finish 会帮忙关闭流
//            if (excelWriter != null) {
//                excelWriter.finish();
//            }
//            //关闭流
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//}
//
//
