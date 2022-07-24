package si.outfit7.types;

/**
 * Class represents Status of supported features
 * @author Goran Corkovic
 */
public class Status {
    private String multiplayer;

    private String userSupport;

    private String ads;

    public String getMultiplayer() {
        return multiplayer;
    }

    public void setMultiplayer(String multiplayer) {
        this.multiplayer = multiplayer;
    }

    public String getUserSupport() {
        return userSupport;
    }

    public void setUserSupport(String userSupport) {
        this.userSupport = userSupport;
    }

    public String getAds() {
        return ads;
    }

    public void setAds(String ads) {
        this.ads = ads;
    }
}
