package bean;

import java.util.List;

/**
 * Created by cxk on 2016/4/29.
 */
public class CategoryFoodBean {
    public List<categoryFood> datas;

    public class categoryFood {
        public String id;
        public String isCollect;
        public String name;
        public String price;
        public String url;
    }
}
