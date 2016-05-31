package bean;

import java.util.List;

/**
 * Created by cxk on 2016/5/12.
 */
public class MySecurityHistoryOrderBean {
    public String userId;
    public String pageNow;
    public String pageSize;
    public String sign;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNow() {
        return pageNow;
    }

    public void setPageNow(String pageNow) {
        this.pageNow = pageNow;
    }
}
