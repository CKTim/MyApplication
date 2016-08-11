package activity;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;


import cn.gdin.hk.hungry.R;
import utils.ManageActivityUtils;

/**
 * Created by cxk on 2016/5/9.
 */
public class ShopMapActivity extends AppCompatActivity implements LocationSource, AMapLocationListener, View.OnClickListener, RouteSearch.OnRouteSearchListener {
    private MapView mapView;
    private AMap aMap;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private LocationSource.OnLocationChangedListener mListener;
    private RouteSearch mRouteSearch;
    private Button btn_walk, btn_back, btn_usecar;
    private UiSettings mUiSettings;
    private LatLonPoint startLatlng;
    private LatLonPoint endLatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopmap_activity);
        ManageActivityUtils.addActivity(this);
        //如果安卓5.0设置状态栏为orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        //初始化各个控件ID
        initID();
        //先初始化mapview，是地图能正常显示出来
        mapView = (MapView) this.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        //得到amap
        aMap = mapView.getMap();
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mUiSettings=aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);//隐藏放大按钮
    }

    private void initID() {
        btn_walk = (Button) this.findViewById(R.id.btn_walk);
        btn_back = (Button) this.findViewById(R.id.btn_back);
        btn_usecar = (Button) this.findViewById(R.id.btn_usecar);
        mRouteSearch = new RouteSearch(ShopMapActivity.this);//初始化routeSearch 对象
        mRouteSearch.setRouteSearchListener(ShopMapActivity.this);//设置数据回调监听器
        btn_walk.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_usecar.setOnClickListener(this);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(true);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                startLatlng = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());//当前位置经纬度
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_walk:
                //开始算步行的路
                //获取传递过来的商家经纬度
                endLatlng = new LatLonPoint(Float.valueOf(getIntent().getStringExtra("endlatidue").toString()), Float.valueOf(getIntent().getStringExtra("endlongitude").toString()));
                RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startLatlng, endLatlng);//始末地址
                RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);//初始化query对象，fromAndTo是包含起终点信息，walkMode是不行路径规划的模式
                mRouteSearch.calculateWalkRouteAsyn(query);//开始算路
                break;
            case R.id.btn_usecar:
                endLatlng = new LatLonPoint(Float.valueOf(getIntent().getStringExtra("endlatidue").toString()), Float.valueOf(getIntent().getStringExtra("endlongitude").toString()));
                RouteSearch.FromAndTo fromAndTo1 = new RouteSearch.FromAndTo(startLatlng, endLatlng);//始末地址
                RouteSearch.DriveRouteQuery query1 = new RouteSearch.DriveRouteQuery(fromAndTo1, RouteSearch.DrivingDefault, null, null, "");
                mRouteSearch.calculateDriveRouteAsyn(query1);
                break;
            case R.id.btn_back:
                this.finish();
                break;
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        aMap.clear();
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    //使用此方法才能将路径在地图上显示出来
                    DrivePath drivePath = result.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            result.getStartPos(),
                            result.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                }
            }
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        aMap.clear();
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    //使用此方法才能将路径在地图上显示出来
                    WalkPath walkPath = result.getPaths().get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            result.getStartPos(),
                            result.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                }
            }
        }
    }
}
