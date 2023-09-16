package cn.idealio.zookeeper;

import org.apache.zookeeper.ZooKeeperMain;
import org.apache.zookeeper.audit.ZKAuditProvider;
import org.apache.zookeeper.server.ExitCode;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.admin.AdminServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2023/9/16
 */
public class ZookeeperServer extends ZooKeeperServerMain {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperServer.class);

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            cli(args);
            return;
        }
        Set<String> argSet = new LinkedHashSet<>(Arrays.asList(args));
        boolean remove = argSet.remove("--start");
        if (!remove) {
            cli(args);
            return;
        }
        String[] array = argSet.toArray(String[]::new);
        startServer(array);
    }

    private static void cli(String[] args) {
        try {
            ZooKeeperMain.main(args);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startServer(String[] args) {
        System.setProperty("zookeeper.admin.enableServer", "false");
        ZookeeperServer server = new ZookeeperServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Shutdown successfully");
        }));
        try {
            server.initializeAndRun(args);
        } catch (IllegalArgumentException var3) {
            LOG.error("Invalid arguments, exiting abnormally", var3);
            LOG.info("Usage: ZooKeeperServerMain configfile | port datadir [ticktime] [maxcnxns]");
            System.err.println("Usage: ZooKeeperServerMain configfile | port datadir [ticktime] [maxcnxns]");
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.INVALID_INVOCATION.getValue());
        } catch (QuorumPeerConfig.ConfigException var4) {
            LOG.error("Invalid config, exiting abnormally", var4);
            System.err.println("Invalid config, exiting abnormally");
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.INVALID_INVOCATION.getValue());
        } catch (FileTxnSnapLog.DatadirException var5) {
            LOG.error("Unable to access datadir, exiting abnormally", var5);
            System.err.println("Unable to access datadir, exiting abnormally");
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.UNABLE_TO_ACCESS_DATADIR.getValue());
        } catch (AdminServer.AdminServerException var6) {
            LOG.error("Unable to start AdminServer, exiting abnormally", var6);
            System.err.println("Unable to start AdminServer, exiting abnormally");
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.ERROR_STARTING_ADMIN_SERVER.getValue());
        } catch (Exception var7) {
            LOG.error("Unexpected exception, exiting abnormally", var7);
            ZKAuditProvider.addServerStartFailureAuditLog();
            ServiceUtils.requestSystemExit(ExitCode.UNEXPECTED_ERROR.getValue());
        }
        LOG.info("Exiting normally");
        ServiceUtils.requestSystemExit(ExitCode.EXECUTION_FINISHED.getValue());
    }
}
