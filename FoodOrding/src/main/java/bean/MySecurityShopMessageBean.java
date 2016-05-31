package bean;

/**
 * Created by cxk on 2016/5/3.
 */
public class MySecurityShopMessageBean {
    public String businessId;
    public String userId;
    public String sign;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
