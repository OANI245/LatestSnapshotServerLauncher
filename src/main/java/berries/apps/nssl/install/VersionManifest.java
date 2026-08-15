package berries.apps.nssl.install;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class VersionManifest {
    @JsonProperty("latest")
    public LatestVersion latest;

    @JsonProperty("versions")
    public List<VersionObject> versions;

    public static class LatestVersion {
        @JsonProperty("snapshot")
        public String snapshotVersion;

        @JsonProperty("release")
        public String releaseVersion;
    }

    public static class VersionObject {
        @JsonProperty("id")
        public String id;

        @JsonProperty("type")
        public String type;

        @JsonProperty("url")
        public String url;
    }
}
