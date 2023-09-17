package cn.idealio.zookeeper;

import jline.console.completer.CandidateListCompletionHandler;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeJNIAccess;
import org.graalvm.nativeimage.hosted.RuntimeResourceAccess;

import javax.servlet.Servlet;

/**
 * @author 宋志宗 on 2023/9/13
 */
public class AOTFeature implements Feature {


    @Override
    public void duringSetup(DuringSetupAccess access) {
        try {
            Module module = AOTFeature.class.getModule();
            RuntimeResourceAccess.addResource(module, "logback.xml");

            Module jlineModule = CandidateListCompletionHandler.class.getModule();
            RuntimeResourceAccess.addResource(
                    jlineModule,
                    "jline/console/completer/CandidateListCompletionHandler.properties"
            );


            Module servletModule = Servlet.class.getModule();
            RuntimeResourceAccess.addResource(servletModule, "javax/servlet/LocalStrings_fr.properties");
            RuntimeResourceAccess.addResource(servletModule, "javax/servlet/LocalStrings_ja.properties");
            RuntimeResourceAccess.addResource(servletModule, "javax/servlet/LocalStrings.properties");
            RuntimeResourceAccess.addResource(servletModule, "javax/servlet/http/LocalStrings_es.properties");
            RuntimeResourceAccess.addResource(servletModule, "javax/servlet/http/LocalStrings_fr.properties");
            RuntimeResourceAccess.addResource(servletModule, "javax/servlet/http/LocalStrings_ja.properties");
            RuntimeResourceAccess.addResource(servletModule, "javax/servlet/http/LocalStrings.properties");

            Class<?> OperatingSystemImplClass = Class.forName("com.sun.management.internal.OperatingSystemImpl");
            RuntimeJNIAccess.register(OperatingSystemImplClass);
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getCommittedVirtualMemorySize0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getFreeMemorySize0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getFreeSwapSpaceSize0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getMaxFileDescriptorCount0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getOpenFileDescriptorCount0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getProcessCpuTime0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getProcessCpuLoad0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getCpuLoad0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getTotalMemorySize0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getTotalSwapSpaceSize0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getSingleCpuLoad0", int.class));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getHostConfiguredCpuCount0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getHostOnlineCpuCount0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("getHostTotalCpuTicks0"));
            RuntimeJNIAccess.register(OperatingSystemImplClass.getDeclaredMethod("initialize0"));
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }
}
