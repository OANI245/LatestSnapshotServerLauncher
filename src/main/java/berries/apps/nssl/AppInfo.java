package berries.apps.nssl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class AppInfo {
    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getVersion() {
        return version;
    }

    public String getVersionName() {
        return versionName;
    }

    public List<Map<String, String>> getAuthors() {
        return authors;
    }

    public int getServiceVersion() {
        return serviceVersion;
    }

    @JsonProperty("name")           protected String name;
    @JsonProperty("name_short")     protected String shortName;
    @JsonProperty("version")        protected String version;
    @JsonProperty("version_name")   protected String versionName;
    @JsonProperty("author")         protected List<Map<String, String>> authors;
    @JsonProperty("service")        protected int serviceVersion;

    protected AppInfo() {}

    public static AppInfo get() {
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(Bootstrap.class.getClassLoader().getResourceAsStream("Application.json")))) {
            ObjectMapper m = new ObjectMapper();
            AppInfo i = m.readValue(r, AppInfo.class);
            if (i == null) {
                throw new IllegalStateException();
            }
            return i;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
