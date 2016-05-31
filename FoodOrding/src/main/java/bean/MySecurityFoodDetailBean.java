package bean;

/**
 * Created by cxk on 2016/5/3.
 */
public class MySecurityFoodDetailBean {
    public String userId;
    public String foodId;
    public String sign;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
