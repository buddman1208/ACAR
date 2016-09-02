package kr.edcan.acar.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chad on 7/4/16.
 */
public class FacebookUser {
    @SerializedName("_json")
    public UserContent content = new UserContent();

    public class UserContent {
        public String name;
        public String id;
        public Picture picture = new Picture();
    }

    public class Picture {
        public Data data = new Data();
    }

    public class Data {
        public boolean is_silhouette;
        public String url;
    }
}