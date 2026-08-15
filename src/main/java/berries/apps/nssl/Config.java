package berries.apps.nssl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Config {
    public static final String CONFIG_PATH = String.format(".%snssl.json", File.separator);
    private static Config instance;

    @JsonProperty("accept")
    public boolean accept = false;

    @JsonProperty("check_updates")
    public boolean checkUpdates = true;

    @JsonProperty("auto_start")
    public boolean autoStart = true;

    @JsonProperty("check_update_time")
    public UpdateLoopStartTime checkUpdateTime = new UpdateLoopStartTime(5, 0, 0);

    @JsonProperty("check_update_interval")
    public long interval = 1000 * 60 * 60 * 24; //默认一天

    @JsonProperty("console_password")
    public long password = -1;

    @JsonProperty("jvm_args")
    public List<String> jvmArgs = new ArrayList<>();

    @JsonProperty("core_args")
    public List<String> coreArgs = new ArrayList<>(List.of("nogui"));

    @JsonProperty("input_encoding")
    public Charset encoding = StandardCharsets.UTF_8;

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

    public static void reset() {
        instance = new Config();
        instance.save();
    }

    public record UpdateLoopStartTime(@JsonProperty("h") int hour,
                                      @JsonProperty("m") int minute,
                                      @JsonProperty("s") int second) {
        @JsonIgnore
        public Calendar getCalendar() {
            var t = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
            t.set(Calendar.HOUR_OF_DAY, hour);
            t.set(Calendar.MINUTE, minute);
            t.set(Calendar.SECOND, second);
            return t;
        }
    }
}
