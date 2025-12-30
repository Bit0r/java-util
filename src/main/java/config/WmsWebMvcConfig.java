package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * url全部加上/test
 */
@Configuration
public class WmsWebMvcConfig implements WebMvcConfigurer {

    private static final String BASE_PATH = "/test";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 添加全局的基础路径
        configurer.setUseTrailingSlashMatch(true)
                .addPathPrefix(BASE_PATH, this::isWmsController);
    }

    // 只对 com.zc.wms 包下的控制器应用前缀
    private boolean isWmsController(Class<?> clazz) {
        // 仅当类位于 com.yi.xm 包下时，才添加路径前缀
        return clazz.getPackage().getName().startsWith("com.yi.xm");
    }

}
