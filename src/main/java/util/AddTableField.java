package util;






import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AddTableField {

    /**
     * 一次性读取全部文件数据
     * @param strFile
     */
    public static void readFile(String strFile) {
        try {
            InputStream is = new FileInputStream(strFile);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            log.info("文件内容:\n" + new String(bytes));
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按行读取文件
     * @param strFile
     */
    public static List<String> readFileByLine(String strFile) {
        List<String> strList = new ArrayList<>();
        try {
            File file = new File(strFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String strLine = null;
            int lineCount = 1;
            while (null != (strLine = bufferedReader.readLine())) {
                strList.add(strLine);
//                log.info("第[" + lineCount + "]行数据:[" + strLine + "]");
//                System.out.println("第[" + lineCount + "]行数据:[" + strLine + "]");
                lineCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strList;
    }

    /**
     * 按行读取全部文件数据
     *
     * @param strFile
     */
    public static StringBuffer readFileAll(String strFile) throws IOException {
        StringBuffer strSb = new StringBuffer();
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
        // character streams
        BufferedReader br = new BufferedReader(inStrR);
        String line = br.readLine();
        while (line != null) {
            strSb.append(line).append("\r\n");
            line = br.readLine();
        }
        return strSb;
    }

    /**
     * 写入文件
     * @param fileName
     * @param s
     * @throws IOException
     */
    public static void writeToFile(String fileName, String s) throws IOException {
        File f1 = new File(fileName);
        OutputStream out = null;
        BufferedWriter bw = null;
        if (f1.exists()) {
            out = new FileOutputStream(f1);
            bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
            bw.write(s);
            bw.flush();
            bw.close();
        } else {
            System.out.println("文件不存在");
        }
    }

    /**
     * 追加文件
     */
    public static void writeToFileAppend(String fileName, String text) {
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(fileName);
            fw = new FileWriter(f, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(text);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改实体类
     */
    public static void upEntityClass(String fileName) {
        //import com.baomidou.mybatisplus.annotation.FieldStrategy;
//        String fileName = "D:\\test-server\\src\\test\\java\\com\\test\\trade\\api\\impl" +
//                "\\PurchaseBusinessShipmentsElementGradePo.java";
        String fileName1 = "D:\\test-server\\src\\test\\java\\com\\test\\trade\\rpc" +
                "\\User1.java";
        List<String> strList = readFileByLine(fileName);
        List<String> returnList = new ArrayList<>();
        StringBuffer strSb = new StringBuffer();
        String fieldStrategy = "import com.baomidou.mybatisplus.annotation.FieldStrategy;";
        String tableField = "import com.baomidou.mybatisplus.annotation.TableField;";
        String ignored = "@TableField(updateStrategy = FieldStrategy.IGNORED)";
        String exist = "@TableField(exist = false)";
        String zc = "zhichong";
        Boolean isFieldStrategy = true;
        Boolean isTableField = true;
        Boolean isZc = false;
        //判断是否存在  fieldStrategy
        if (strList.contains(fieldStrategy)) {
            isFieldStrategy = false;
        }
        //判断是否存在  tableField
        if (strList.contains(tableField)) {
            isTableField = false;
        }
        //判断是否存在 有些字段已经添加了 ignored
        List<Integer> ignoredList = new ArrayList<>();
        List<Integer> existList = new ArrayList<>();

        for (int i = 0; i < strList.size(); i++) {
            String data = strList.get(i);
            if(data.contains(zc)){
                isZc = true;
            }

            if (data.contains(ignored) || data.contains(exist)) {
                Boolean is = true;
                while (is) {
                    i++;
                    if (i == strList.size()) {
                        is = false;
                    }
                    data = strList.get(i);
                    if (data.contains("private ")) {
                        ignoredList.add(i);
                        is = false;
                    }
                }
            }
        }
        if(isZc != true){
            return;
        }

        //插入数据
        for (int i = 0; i < strList.size(); i++) {
            //tt.endsWith(",");
            String data = strList.get(i);

            if (data.contains("import ") && isFieldStrategy) {
                strSb.append(fieldStrategy).append("\r\n");
                isFieldStrategy = false;
            }

            if (data.contains("import ") && isTableField) {
                strSb.append(tableField).append("\r\n");
                isTableField = false;
            }
            if (data.contains("private ")) {
                if (!data.contains(" String ") && !data.contains(" final ")) {
                    if (!ignoredList.contains(i)) {
                        System.out.println(data);
                        String st1 = data.substring(0, data.indexOf("private"));
                        System.out.println(data.substring(0, data.indexOf("private")));
                        strSb.append(st1).append("@TableField(updateStrategy = FieldStrategy.IGNORED)").append("\r\n");
//                   returnList.add("@TableField(updateStrategy = FieldStrategy.IGNORED)");
                    }

                }
            }
            returnList.add(data);
            strSb.append(data).append("\r\n");
        }

//        System.out.println(strSb.toString());
        writeToFileAppend(fileName, strSb.toString());
    }

    /**
     * 读取某个文件夹下的所有文件
     */
    public static List<String> readfile(String filepath) throws FileNotFoundException, IOException {
        List<String> fileNames = new ArrayList<>();
        File file = new File(filepath);
        if (!file.isDirectory()) {
            System.out.println("文件");
            System.out.println("path=" + file.getPath());
            System.out.println("absolutepath=" + file.getAbsolutePath());
            System.out.println("name=" + file.getName());
        } else if (file.isDirectory()) {
            System.out.println("文件夹？？？？");
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + "\\" + filelist[i]);
                if (!readfile.isDirectory()) {
                    String fileName = readfile.getPath();
                    System.out.println("path=" + fileName);
                    fileNames.add(fileName);
//                        System.out.println("absolutepath="
//                                + readfile.getAbsolutePath());
//                        System.out.println("name=" + readfile.getName());

                } else if (readfile.isDirectory()) {
                    //文件下面的文件夹 暂时不要
//                        readfile(filepath + "\\" + filelist[i]);
                }
            }

        }


        return fileNames;
    }

    public static void main(String[] args) throws IOException {

        String fileName = "D:\\java\\po";
//        List<String> fileNames = readfile(fileName);
        List<String> fileNames = readfile(fileName);
        for(String data :fileNames ){
            upEntityClass(data);
        }
        System.out.println("00===" + fileNames);




    }
}


