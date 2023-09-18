package cn.idealio.zookeeper;

import org.apache.zookeeper.ZooKeeperMain;
import org.apache.zookeeper.audit.ZKAuditProvider;
import org.apache.zookeeper.client.FourLetterWordMain;
import org.apache.zookeeper.common.X509Exception;
import org.apache.zookeeper.server.ExitCode;
import org.apache.zookeeper.server.admin.AdminServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.apache.zookeeper.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2023/9/16
 */
public class ZookeeperServer extends QuorumPeerMain {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperServer.class);

    public static void main(String[] args) {
        Set<String> argSet = new LinkedHashSet<>(Arrays.asList(args));
        // 执行命令行
        if (CommandLineHandler.exec(argSet)) {
            return;
        }

        // status
        if (argSet.remove("--status")) {
            status(argSet);
            return;
        }

        // cli
        if (argSet.remove("--cli")) {
            cli(argSet);
            return;
        }

        // start server
        startServer(argSet);
    }

    private static void status(@Nonnull Set<String> argSet) {
        try {
            String[] args = argSet.toArray(String[]::new);
            if (args.length == 0) {
                System.out.println(FourLetterWordMain.send4LetterWord("127.0.0.1", 2181, "srvr"));
            } else if (args.length == 1) {
                System.out.println(FourLetterWordMain.send4LetterWord("127.0.0.1", Integer.parseInt(args[0]), "srvr"));
            } else if (args.length == 2) {
                System.out.println(FourLetterWordMain.send4LetterWord(args[0], Integer.parseInt(args[1]), "srvr"));
            } else if (args.length == 3) {
                System.out.println(FourLetterWordMain.send4LetterWord(args[0], Integer.parseInt(args[1]), "srvr", Boolean.parseBoolean(args[2])));
            } else {
                System.out.println("Usage: --status <host> <port> <secure(optional)>");
                System.out.println("Usage: --status, same as --status 127.0.0.1 2181");
                System.out.println("Usage: --status 2181, same as --status 127.0.0.1 2181");
            }
        } catch (IOException | X509Exception.SSLContextException e) {
            throw new RuntimeException(e);
        }
    }

    private static void cli(@Nonnull Set<String> argSet) {
        try {
            ZooKeeperMain.main(argSet.toArray(String[]::new));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startServer(@Nonnull Set<String> argSet) {
        if (argSet.isEmpty()) {
            argSet.add("./conf/zoo.cfg");
        }
        String[] args = argSet.toArray(String[]::new);

        System.setProperty("zookeeper.admin.enableServer", "false");
        ZookeeperServer server = new ZookeeperServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Shutdown successfully")));
        try {
            server.initializeAndRun(args);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid arguments, exiting abnormally", e);
            String usage = "Usage: QuorumPeerMain configfile";
            LOG.info(usage);
            System.err.println(usage);
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.INVALID_INVOCATION.getValue());
        } catch (QuorumPeerConfig.ConfigException e) {
            LOG.error("Invalid config, exiting abnormally", e);
            System.err.println("Invalid config, exiting abnormally");
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.INVALID_INVOCATION.getValue());
        } catch (FileTxnSnapLog.DatadirException e) {
            LOG.error("Unable to access datadir, exiting abnormally", e);
            System.err.println("Unable to access datadir, exiting abnormally");
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.UNABLE_TO_ACCESS_DATADIR.getValue());
        } catch (AdminServer.AdminServerException e) {
            LOG.error("Unable to start AdminServer, exiting abnormally", e);
            System.err.println("Unable to start AdminServer, exiting abnormally");
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.ERROR_STARTING_ADMIN_SERVER.getValue());
        } catch (Exception e) {
            LOG.error("Unexpected exception, exiting abnormally", e);
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.UNEXPECTED_ERROR.getValue());
        }
        LOG.info("Exiting normally");
        ServiceUtils.requestSystemExit(ExitCode.EXECUTION_FINISHED.getValue());
    }
}
