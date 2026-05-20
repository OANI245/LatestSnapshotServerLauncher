package berries.apps.nssl;

import java.io.File;

public interface Paths {
    String SERVER_FILE_PATH = String.format(".%sserver.jar", File.separator);
    String FABRIC_FILE_PATH = String.format(".%sfabric-server-launch.jar", File.separator);
}
