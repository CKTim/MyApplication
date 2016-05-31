package bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cxk on 2016/4/23.
 */
public class OrderListBean implements Serializable {
    public int foodId;
    public int num;
    public String special;

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
