package berries.apps.nssl.install;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class FileDownloader {
    private static final int BUFFER_SIZE = 8192;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 30000;

    public static void startDownload(String url, File saveFile, DownloadProgressListener listener) throws Exception {
        // 创建父目录（防止路径不存在）
        Files.createDirectories(saveFile.getParentFile().toPath());

        HttpURLConnection conn = null;
        try {
            // 1. 打开连接
            URL downloadUrl = new URL(url);
            conn = (HttpURLConnection) downloadUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);  // 连接超时 15秒
            conn.setReadTimeout(READ_TIMEOUT);     // 读取超时 30秒

            // 2. 检查 HTTP 状态码（必须 200 才下载）
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Download Failed: " + responseCode + " URL: " + url);
            }

            long totalSize = conn.getContentLengthLong();
            long currentSize = 0;

            // 3. 获取输入流，自动关闭，写入文件
            try (InputStream in = conn.getInputStream();
                 var out = Files.newOutputStream(saveFile.toPath(),
                         StandardOpenOption.CREATE,
                         StandardOpenOption.TRUNCATE_EXISTING)) {

                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    currentSize += len;

                    // 计算进度
                    double progress = totalSize <= 0 ? 0 : (double) currentSize / totalSize;
                    // 回调进度
                    if (listener != null) {
                        listener.onProgress((int) (progress * 100));
                    }
                }
            }

            //System.out.println("Download file: " + saveFile.getAbsolutePath());

        } finally {
            // 确保连接关闭
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
