package adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.cxk.myapplication.R;


public class MyListViewAdapter extends BaseAdapter {
	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;

	public MyListViewAdapter(Context context, List<Map<String, Object>> list) {
		mInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.custom_home_listview, null);
			holder.iv_restaurant = (ImageView) convertView.findViewById(R.id.iv_restaurant);
			holder.tv_restaurant = (TextView) convertView.findViewById(R.id.tv_restaurant_name);
			holder.tv_Foodprices = (TextView) convertView.findViewById(R.id.tv_food_prices);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.image1)
//				.showImageOnFail(R.drawable.image2).cacheInMemory(true).build();
//		ImageLoader.getInstance().displayImage((String) list.get(position).get("ShopPic"), holder.iv_restaurant,
//				options);
//		holder.tv_restaurant.setText((String) list.get(position).get("ShopName"));
		return convertView;
	}

	class ViewHolder {
		//餐厅或者食物照片
		ImageView iv_restaurant;
		// 餐厅或者食物名字
		TextView tv_restaurant;
		// 食物价格
		TextView tv_Foodprices;

	}
}
