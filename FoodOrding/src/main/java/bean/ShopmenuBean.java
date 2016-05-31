package bean;

import java.util.List;

/**
 * Created by cxk on 2016/4/19.
 */
public class ShopmenuBean {
    public List<category> datas;

    public class category {
        public List<menu> list;
        public String type;
    }
    public class menu{
        public String id;
        public String isCollect;
        public String name;
        public String price;
        public List<String> special;
        public String url;
    }

}
