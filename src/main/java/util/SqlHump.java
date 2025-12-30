package util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlHump {
    public static void main(String[] args) {
        String str = "a.id,\n" +
                "a.id as order_material_id,\n" +
                "k.payment_date as balance_payment_date";
        String[] strings = str.split(",");
        List<String> stringList = new ArrayList<>();
        for (String data : strings){

            if(data.indexOf("a.material_id") >=0){
//                System.out.println(data);
            }
            if(data.indexOf(" as ")>=0){
                String[] strings1 = data.split("as");
                String j = underlineToHump(strings1[1]);
                data = strings1[0] +" as " + j;

            }else {
                String j =   data.substring(data.indexOf(".")+1);
                j = underlineToHump(j);
                data = data +" as " + j;

            }
            stringList.add(data);
        }

        String sql  =stringList.stream().collect(Collectors.joining(","));
        System.out.println(sql);
    }




    private static Pattern UNDERLINE_PATTERN = Pattern.compile("_([a-z])");


    /**
     * 根据传入的带下划线的字符串转化为驼峰格式
     * @param str
     * @author mrf
     * @return
     */

    public static String underlineToHump(String str) {
        //正则匹配下划线及后一个字符，删除下划线并将匹配的字符转成大写
        Matcher matcher = UNDERLINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配的子串替换成指定字符串，并且将替换后的子串及之前到上次匹配的子串之后的字符串添加到StringBuffer对象中
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            //把之后的字符串也添加到StringBuffer对象中
            matcher.appendTail(sb);
        } else {
            //去除除字母之外的前面带的下划线
            return sb.toString().replaceAll("_", "");
        }
        return underlineToHump(sb.toString());
    }

}

