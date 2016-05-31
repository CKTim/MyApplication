package bean;

import java.util.List;

/**
 * Created by cxk on 2016/5/10.
 */
public class OrderDetailBean {
    public int amount;
    public int businessid;
    public String  businessname;
    public int iscomment;
    public String  url;
    public int isfinish;
    public int orderid;
    public List<myfood> foodlist;
    public class myfood{
        public String name;
        public String price;
        public String special;
        public String num;
    }


}
