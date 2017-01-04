package tracker.health.buzzapps.ua.heathtracker.Model;

/**
 * Created by Admin on 20.11.2016.
 */

public class User {

    private String name;
    private String email;
    private String userid;
    private String providerId;
    private String photoUrl;
    private String team;

    public User(String name, String email, String providerId, String photoUrl,String team){
        this.name = name;
        this.email = email;
        this.providerId = providerId;
        this.photoUrl = photoUrl;
    }

    public User(){

    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
