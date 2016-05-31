package bean;

/**
 * Created by cxk on 2016/5/3.
 */
public class MySecurityNeabyAllListBean {
    public String userId;
    public int    raidus;
    public String pageNow;
    public String pageSize;
    public String sign;
    public double lat;
    public double lon;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRaidus() {
        return raidus;
    }

    public void setRaidus(int raidus) {
        this.raidus = raidus;
    }

    public String getPageNow() {
        return pageNow;
    }

    public void setPageNow(String pageNow) {
        this.pageNow = pageNow;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
