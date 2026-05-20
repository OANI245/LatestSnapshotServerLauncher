package berries.apps.nssl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class Config {
    public static final String CONFIG_PATH = String.format(".%snssl.json", File.separator);
    private static Config instance;

    @JsonProperty("accept")
    public boolean accept = false;

    @JsonProperty("check_updates")
    public boolean checkUpdates = true;

    @JsonProperty("console_password")
    public long password = -1;

    @JsonProperty("jvm_args")
    public String[] jvmArgs = {};

    @JsonProperty("core_args")
    public String[] coreArgs = { "nogui" };

    public static Config getInstance() {
        if (instance == null) {
            read();
        }
        return instance;
    }

    public static void read() {
        try {
            File config = new File(CONFIG_PATH);
            if (!config.exists()) {
                if (!config.createNewFile()) {
                    throw new RuntimeException();
                }
                instance = new Config();
                instance.save();
                return;
            }

            try (BufferedReader r = new BufferedReader(new FileReader(config))) {
                instance = new ObjectMapper().readValue(r, Config.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            File config = new File(CONFIG_PATH);
            if (!config.exists()) {
                if (!config.createNewFile()) {
                    throw new RuntimeException();
                }
            }

            try (BufferedWriter w = new BufferedWriter(new FileWriter(config))) {
                ObjectMapper m = new ObjectMapper();
                m.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
                m.writeValue(w, this);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
