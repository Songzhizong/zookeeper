package cn.idealio.zookeeper;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeResourceAccess;

/**
 * @author 宋志宗 on 2023/9/13
 */
public class AOTFeature implements Feature {


    @Override
    public void duringSetup(DuringSetupAccess access) {
        Module module = AOTFeature.class.getModule();
        RuntimeResourceAccess.addResource(module, "logback.xml");

        RuntimeResourceAccess.addResource(
                jline.console.completer.CandidateListCompletionHandler.class.getModule(),
                "jline/console/completer/CandidateListCompletionHandler.properties"
        );
    }
}
