package cn.idealio.zookeeper;


import org.apache.zookeeper.common.StringUtils;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author 宋志宗 on 2023/9/7
 */
public class CommandLineHandler {
    private static final String PROJECT_NAME = "native-zookeeper";

    public static boolean exec(@Nonnull Set<String> args) {
        for (String arg : args) {
            if (StringUtils.isBlank(arg)) {
                help();
                return false;
            }
            if ("--help".equals(arg) || "-h".equals(arg)) {
                help();
                return false;
            }
            if ("--all".equals(arg) || "-a".equals(arg)) {
                info();
                return false;
            }
            if ("--version".equals(arg) || "-v".equals(arg)) {
                version();
                return false;
            }
        }
        return true;
    }

    public static void help() {
        System.out.println(" -a, --all      : print all information");
        System.out.println(" -h, --help     : display this help and exit");
        System.out.println(" -v, --version  : print version");
    }

    static void info() {
        String version = Version.getVersion();
        String buildTime = Version.formatBuildTime("yyyy-MM-dd_HH:mm");
        String osName = System.getProperty("os.name");
        if ("Mac OS X".equals(osName)) {
            osName = "MacOS";
        }
        String arch = System.getProperty("os.arch");
        String vmVersion = System.getProperty("java.vm.version");
        System.out.println(PROJECT_NAME + " version " + version + " " + buildTime + " " + osName + "/" + arch + " vm " + vmVersion);
        String zookeeperFullVersion = org.apache.zookeeper.Version.getFullVersion();
        System.out.println("zookeeper " + zookeeperFullVersion);
    }

    static void version() {
        String version = Version.getBuildVersion();
        System.out.println(version);
    }
}
