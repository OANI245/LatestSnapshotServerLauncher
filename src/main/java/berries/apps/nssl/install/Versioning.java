package berries.apps.nssl.install;

import berries.apps.nssl.console.output.Printers;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static berries.apps.nssl.Paths.*;

public interface Versioning {
    static boolean checkInstalled() {
        return new File(SERVER_FILE_PATH).exists() && new File(FABRIC_FILE_PATH).exists();
    }

    static String getMinecraftVersion(boolean bl) {
        try (ZipInputStream z = new ZipInputStream(new FileInputStream(SERVER_FILE_PATH), StandardCharsets.UTF_8)) {
            ZipEntry e;

            while ((e = z.getNextEntry()) != null) {
                if (e.getName().equals("version.json")) try (BufferedReader br = new BufferedReader(new InputStreamReader(z))) {
                    ExistServerVersion esv = new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(br, ExistServerVersion.class);
                    if (bl) {
                        Printers.LOGGER.info("Current Minecraft Version: {}", esv.id);
                    }
                    return esv.id;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "11w47a";
    }

    static VersionResult getLatestMinecraftVersion(boolean bl) {
        try {
            VersionManifest manifest = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new URL("https://piston-meta.mojang.com/mc/game/version_manifest.json"), VersionManifest.class);
            if (manifest.latest.snapshotVersion == null) {
                return new VersionResult(getMinecraftVersion(false), null);
            }
            VersionManifest.VersionObject selected = manifest.versions.getFirst();
            if (selected == null) {
                return null;
            }
            if (bl) {
                Printers.LOGGER.info("Latest Minecraft Version: {}", selected.id);
            }
            return new VersionResult(selected.id, new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new URL(selected.url), VersionMeta.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static boolean shouldUpdate() {
        var r = getLatestMinecraftVersion(true);
        if (r == null) {
            return false;
        }
        boolean bl = !r.id().trim().toLowerCase(Locale.ENGLISH).equals(getMinecraftVersion(true).trim().toLowerCase(Locale.ENGLISH));
        return bl;
    }
}
