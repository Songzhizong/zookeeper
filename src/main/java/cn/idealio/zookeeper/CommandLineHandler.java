package cn.idealio.zookeeper;


import org.apache.zookeeper.common.StringUtils;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author 宋志宗 on 2023/9/7
 */
public class CommandLineHandler {
    private static final String PROJECT_NAME = "zookeeper";

    public static boolean exec(@Nonnull Set<String> args) {
        boolean exit = false;
        for (String arg : args) {
            if (StringUtils.isBlank(arg)) {
                help();
                exit = true;
            }
            if ("--help".equals(arg) || "-h".equals(arg)) {
                help();
                exit = true;
            }
            if ("--all".equals(arg) || "-a".equals(arg)) {
                info();
                exit = true;
            }
            if ("--version".equals(arg) || "-v".equals(arg)) {
                version();
                exit = true;
            }
        }
        if (exit) {
            System.exit(0);
        }
        //noinspection ConstantValue
        return exit;
    }

    public static void help() {
        System.out.println(" -a, --all      : print all information");
        System.out.println(" -h, --help     : display this help and exit");
        System.out.println(" -v, --version  : print version");
    }

    static void info() {
        String version = Version.getVersion();
        String buildTime = Version.getBuildTime();
        String osName = System.getProperty("os.name");
        if ("Mac OS X".equals(osName)) {
            osName = "MacOS";
        }
        String arch = System.getProperty("os.arch");
        String vmVersion = System.getProperty("java.vm.version");
        System.out.println(PROJECT_NAME + " version " + version + " " + buildTime + " " + osName + "/" + arch + " vm " + vmVersion);
        String zookeeperFullVersion = org.apache.zookeeper.Version.getFullVersion();
        System.out.println("Apache Zookeeper " + zookeeperFullVersion);
    }

    static void version() {
        String version = Version.getBuildVersion();
        System.out.println(version);
    }
}
