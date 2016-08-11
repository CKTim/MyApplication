package activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import cn.gdin.hk.hungry.R;
import fragment.Cuisine_fragment;
import fragment.District_fragment;
import fragment.Restaurant_fragment;
import utils.ManageActivityUtils;

public class CategoryActivity extends FragmentActivity implements OnClickListener {
	private Button btn_opendrawer, btn_restaurant, btn_cuisine, btn_district,btn_openMyLove;
	private DrawerLayout drawerlayout;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private Cuisine_fragment cuisine_fragment;
	private District_fragment district_fragment;
	private Restaurant_fragment restaurant_fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_activity);
		ManageActivityUtils.addActivity(this);
		//如果安卓5.0设置状态栏为orange
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
		}
		//初始化ID
		initIDs();
		//检查哪个button传进来的
		checkButton();

	}


	private void checkButton() {
		//初始化各个fragment
		cuisine_fragment = new Cuisine_fragment();
		restaurant_fragment = new Restaurant_fragment();
		district_fragment = new District_fragment();
		manager = getSupportFragmentManager();
		transaction = manager.beginTransaction();
		String ButtonId = getIntent().getStringExtra("buttonID").trim();
		if (ButtonId.equals("a") || ButtonId.equals("d")) {
			btn_cuisine.setSelected(true);
			transaction.add(R.id.rl_fragment_container, cuisine_fragment);
		} else if (ButtonId.equals("b") || ButtonId.equals("e")) {
			btn_restaurant.setSelected(true);
			transaction.add(R.id.rl_fragment_container, restaurant_fragment);
		} else if (ButtonId.equals("c") || ButtonId.equals("f")) {
			btn_district.setSelected(true);
			transaction.add(R.id.rl_fragment_container, district_fragment);
		}
		transaction.commit();
	}



	private void initIDs() {
		// TODO Auto-generated method stub
		btn_opendrawer = (Button) this.findViewById(R.id.btn_opendrawer);
		btn_restaurant = (Button) this.findViewById(R.id.btn_restaurant);
		btn_cuisine = (Button) this.findViewById(R.id.btn_cuisine);
		btn_district = (Button) this.findViewById(R.id.btn_district);
		drawerlayout = (DrawerLayout) this.findViewById(R.id.drawerlayout);
		btn_openMyLove=(Button)this.findViewById(R.id.btn_openmylove);
		btn_opendrawer.setOnClickListener(this);
		btn_restaurant.setOnClickListener(this);
		btn_cuisine.setOnClickListener(this);
		btn_district.setOnClickListener(this);
		btn_openMyLove.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		transaction = manager.beginTransaction();
		switch (v.getId()) {
		case R.id.btn_opendrawer:
			drawerlayout.openDrawer(Gravity.LEFT);
			break;
		case R.id.btn_openmylove:
			Intent intent=new Intent(CategoryActivity.this,MyLoveActivity.class);
			startActivity(intent);
			break;

		case R.id.btn_cuisine:
			btn_cuisine.setSelected(true);
			//将按钮颜色改变
			if (btn_restaurant.isSelected()) {
				btn_restaurant.setSelected(false);
			} else if (btn_district.isSelected()) {
				btn_district.setSelected(false);
			}

			//加载分类fragment，隐藏其他fragment
			if (cuisine_fragment.isAdded()) {
				transaction.hide(restaurant_fragment);
				transaction.hide(district_fragment);
				transaction.show(cuisine_fragment);
			} else {
				transaction.hide(restaurant_fragment);
				transaction.hide(district_fragment);
				transaction.add(R.id.rl_fragment_container, cuisine_fragment);
				transaction.show(cuisine_fragment);
			}
			break;
		case R.id.btn_restaurant:
			btn_restaurant.setSelected(true);
			//将按钮颜色改变
			if (btn_cuisine.isSelected()) {
				btn_cuisine.setSelected(false);
			} else if (btn_district.isSelected()) {
				btn_district.setSelected(false);
			}

			//加载餐厅fragment，隐藏其他fragment
			if (restaurant_fragment.isAdded()) {
				transaction.hide(cuisine_fragment);
				transaction.hide(district_fragment);
				transaction.show(restaurant_fragment);
			} else {
				transaction.hide(cuisine_fragment);
				transaction.hide(district_fragment);
				transaction.add(R.id.rl_fragment_container, restaurant_fragment);
				transaction.show(restaurant_fragment);
			}
			break;
		case R.id.btn_district:
			btn_district.setSelected(true);
			//将按钮颜色改变
			if (btn_restaurant.isSelected()) {
				btn_restaurant.setSelected(false);
			} else if (btn_cuisine.isSelected()) {
				btn_cuisine.setSelected(false);
			}

			//加载地区fragment，隐藏其他fragment
			if (district_fragment.isAdded()) {
				transaction.hide(cuisine_fragment);
				transaction.hide(restaurant_fragment);
				transaction.show(district_fragment);
			} else {
				transaction.hide(cuisine_fragment);
				transaction.hide(restaurant_fragment);
				transaction.add(R.id.rl_fragment_container, district_fragment);
				transaction.show(district_fragment);
			}
			break;

		}
		transaction.commit();
	}


}
