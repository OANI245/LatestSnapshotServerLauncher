package berries.apps.nssl;

import java.io.File;

public interface Paths {
    String SERVER_FILE_PATH = String.format(".%sserver.jar", File.separator);
    String SERVER_PROPERTIES_PATH = String.format(".%sserver.properties", File.separator);
    String FABRIC_FILE_PATH = String.format(".%sfabric-server-launch.jar", File.separator);
    String FABRIC_INSTALLER_FILE_PATH = String.format(".%sfabric-installer.jar", File.separator);
}
