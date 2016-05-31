package bean;

/**
 * Created by cxk on 2016/5/3.
 */
public class MySecurityHeatBean {
    public String userId;
    public String  pageNow;
    public String pageSize;
    public String sign;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
