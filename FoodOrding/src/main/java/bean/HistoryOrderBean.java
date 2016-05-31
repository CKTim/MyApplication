package bean;

import java.util.List;

/**
 * Created by cxk on 2016/5/12.
 */
public class HistoryOrderBean {
    public List<myhistory> datas;
    public class myhistory{
        public int amount;
        public String  businessname;
        public int iscomment;
        public String  url;
        public int isfinish;
        public int orderid;
    }
}
