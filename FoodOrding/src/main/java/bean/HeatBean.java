package bean;

import java.util.List;

/**
 * Created by cxk on 2016/4/18.
 */
public class HeatBean {
    public List<allheat> datas;

    public class allheat {
        public String id;
        public String isCollect;
        public String name;
        public String type;
        public String price;
        public String url;
    }

}
