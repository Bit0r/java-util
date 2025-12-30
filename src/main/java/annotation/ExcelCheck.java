package annotation;



import java.lang.annotation.*;

/**
 * excel 校验
 * @author zhichong
 */
@Documented
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})  //可以在字段、枚举的常量、方法
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCheck {

    //不能为空
    boolean isNotNull() default true;

    //整数长度
    int integer() default -1;

    //小数长度
    int decimal() default -1;

    //时间格式
    String dateFormat() default "";

    //正则校验
    String matches() default "";

    //字符串长度
    int maxLength() default -1;

}

