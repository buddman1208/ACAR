package kr.edcan.acar.models;

/**
 * Created by JunseokOh on 2016. 9. 3..
 */
public class User {

    private String id, name, gcm_token;

    public User(String id, String name, String gcm_token) {
        this.id = id;
        this.name = name;
        this.gcm_token = gcm_token;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGcm_token() {
        return gcm_token;
    }
}
