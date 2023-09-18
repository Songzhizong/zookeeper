package cn.idealio.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author 宋志宗 on 2023/9/18
 */
public class Version {
    public static final String UNKNOWN = "unknown";
    public static final String ENV_PROPERTIES_NAME = "zk_env.properties";
    public static final String TZ_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final Logger log = LoggerFactory.getLogger(Version.class);
    private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";
    private static final int SNAPSHOT_SUFFIX_LENGTH = SNAPSHOT_SUFFIX.length();
    @Nullable
    private static volatile Properties envProperties = null;
    private static volatile boolean envRead = false;
    @Nullable
    private static volatile LocalDateTime buildTime = null;
    private static volatile boolean buildTimeRead = false;
    @Nonnull
    private static final String BUILD_VERSION = getBuildVersion0();

    @Nonnull
    public static String getVersion() {
        Properties properties = getEnvProperties();
        if (properties == null) {
            return UNKNOWN;
        }
        String version = properties.getProperty("jar.version");
        if (version == null || version.isBlank()) {
            return UNKNOWN;
        }
        return "v" + version;
    }

    @Nonnull
    public static String getBuildVersion() {
        return BUILD_VERSION;
    }

    @Nonnull
    private static String getBuildVersion0() {
        String pattern = "yyMMddHHmm";
        String version = getVersion();
        if (version.toUpperCase().endsWith(SNAPSHOT_SUFFIX)) {
            String buildTimeStr = formatBuildTime(pattern);
            if (buildTimeStr == null) {
                log.warn("构建时间为空, 无法生成带时间快照版本号, version: {}", version);
                return version;
            }
            String substring = version.substring(0, version.length() - SNAPSHOT_SUFFIX_LENGTH);
            return substring + "s" + buildTimeStr;
        }
        if (version.equals(UNKNOWN)) {
            String buildTimeStr = formatBuildTime(pattern);
            if (buildTimeStr == null) {
                return version;
            }
            return version + "s" + buildTimeStr;
        }
        return version;
    }

    @Nullable
    public static LocalDateTime getBuildTime() {
        if (Version.buildTimeRead) {
            return Version.buildTime;
        }
        Version.buildTimeRead = true;
        if (Version.buildTime != null) {
            return Version.buildTime;
        }
        LocalDateTime buildTime = getBuildTime0();
        Version.buildTime = buildTime;
        return buildTime;
    }

    @Nullable
    public static String formatBuildTime(@Nonnull String pattern) {
        LocalDateTime buildTime = getBuildTime();
        if (buildTime == null) {
            log.warn("构建时间为空, 无法格式化, pattern: {}", pattern);
            return null;
        }
        return buildTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    @Nullable
    private static LocalDateTime getBuildTime0() {
        Properties properties = getEnvProperties();
        if (properties == null) {
            return null;
        }
        String buildTime = properties.getProperty("maven.buildTime");
        if (buildTime == null || buildTime.isBlank()) {
            log.warn("读取构建时间失败, maven.buildTime 为空");
            return null;
        }
        if (buildTime.contains("@")) {
            log.warn("读取构建时间失败, maven.buildTime 为占位符");
            return null;
        }
        LocalDateTime parsed = LocalDateTime.parse(buildTime, DateTimeFormatter.ofPattern(TZ_DATE_TIME));
        TimeZone timeZone = TimeZone.getDefault();
        int rawOffset = timeZone.getRawOffset();
        return parsed.plusSeconds(rawOffset / 1000);
    }

    @Nullable
    private static Properties getEnvProperties() {
        if (Version.envRead) {
            return Version.envProperties;
        }
        Version.envRead = true;
        if (Version.envProperties != null) {
            return Version.envProperties;
        }
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(ENV_PROPERTIES_NAME);
        if (inputStream == null) {
            log.warn("读取构建时间失败, {} 不存在", ENV_PROPERTIES_NAME);
            return null;
        }
        try (inputStream) {
            Properties properties = new Properties();
            properties.load(inputStream);
            Version.envProperties = properties;
            return properties;
        } catch (IOException e) {
            log.warn("读取 {} 失败", ENV_PROPERTIES_NAME, e);
            return null;
        }
    }
}
