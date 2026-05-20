package berries.apps.nssl.install;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@FunctionalInterface
public interface DownloadProgressListener {
    void onProgress(int progress);
}