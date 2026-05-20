package berries.apps.nssl.install;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static berries.apps.nssl.Paths.*;

public interface Versioning {
    static boolean checkInstalled() {
        return new File(SERVER_FILE_PATH).exists() && new File(FABRIC_FILE_PATH).exists();
    }

    static String getMinecraftVersion() {
        try (ZipInputStream z = new ZipInputStream(new FileInputStream(SERVER_FILE_PATH), StandardCharsets.UTF_8)) {
            ZipEntry e;

            while ((e = z.getNextEntry()) != null) {
                if (e.getName().equals("version.json")) try (BufferedReader br = new BufferedReader(new InputStreamReader(z))) {
                    return new ObjectMapper().readValue(br, ExistServerVersion.class).id;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "11w47a";
    }

    static VersionResult getLatestMinecraftVersion() {
        try {
            VersionManifest manifest = new ObjectMapper().readValue(new URL("https://piston-meta.mojang.com/mc/game/version_manifest.json"), VersionManifest.class);
            VersionManifest.VersionObject selected = null;
            for (VersionManifest.VersionObject version : manifest.versions) {
                if (version.id.equals(manifest.latest.snapshotVersion) && version.type.equals("snapshot")) {
                    selected = version;
                    break;
                }
            }
            if (selected == null) {
                return null;
            }
            return new VersionResult(selected.id, new ObjectMapper().readValue(new URL(selected.url), VersionMeta.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static boolean shouldUpdate() {
        var r = getLatestMinecraftVersion();
        if (r == null) {
            return false;
        }
        return r.id().equals(getMinecraftVersion());
    }
}
