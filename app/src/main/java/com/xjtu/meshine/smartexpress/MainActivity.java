package com.xjtu.meshine.smartexpress;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "SmartExpress";

    private TextView tvResult;
    private Button btnStartPlan;
    private ProgressBar progressBar;
    private Button btnReset;

    private MapView mapView = null;
    private BaiduMap map = null;
    private MyLocationData myLocationData = null;

    private LocationService locationService = null;


    private RoutePlanSearch routePlanSearch = null;
    private MyRoutePlanResultListener routePlanResultListener = null;


    DistanceManager distanceManager;
    private List<Marker> markers = new ArrayList<>();
    List<LatLng> nodes = new ArrayList<>();
    private int[][] matrix = null;
    private int matrixLen;
    private LatLng source = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        initComponent();


    }

    private void initComponent() {
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnStartPlan = (Button) findViewById(R.id.btn_start_plan);
        btnStartPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlan();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        btnReset = (Button) findViewById(R.id.btn_reset_plan);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               reset();
            }
        });



        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.bmapView);
        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        map.setOnMapLongClickListener(onMapLongClickListener);
        // -----------location config ------------
        locationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();

        routePlanSearch = RoutePlanSearch.newInstance();

        routePlanResultListener = new MyRoutePlanResultListener();
        routePlanSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
    }

    /**
     * 重置
     */
    private void reset() {
        iconIndex = 0;
        markers.clear();
        nodes.clear();
        map.clear();
        distanceManager.reset();
        tvResult.setText("");
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * 开始规划
     */
    private void startPlan() {
        progressBar.setVisibility(View.VISIBLE);
        source = new LatLng(myLocationData.latitude,myLocationData.longitude);
        nodes.add(source);
        for (Marker m : markers){
            nodes.add(m.getPosition());
        }

//        for (LatLng l : nodes){
//            Log.i(TAG,"("+l.longitude+","+l.latitude+")");
//        }
        matrixLen = nodes.size()+1;
        matrix = new int[matrixLen][matrixLen];
        distanceManager = DistanceManager.getInstance();
        distanceManager.setMatrix(matrix);

        for (int i=0;i<matrixLen;i++){
            for (int j = 0;j<matrixLen;j++){
                if (i == 0 || j == 0){//占位
                    distanceManager.addDistance(new Distance(i,j,0,true,null,null,null));
                }else{
                        if (i == j){
                            distanceManager.addDistance(new Distance(i,j,-1,true,nodes.get(i-1),nodes.get(j-1),null));
                        }else{
                            distanceManager.addDistance(new Distance(i,j,0,false,nodes.get(i-1),nodes.get(j-1),null));
                        }
                }

            }
        }

//        distanceManager.showMatrix();

        if (!distanceManager.isUnsearchedEmpty()){
            handler.sendEmptyMessage(START_SEARCH);
        }else {
            searchComplete();
        }

    }




    private final static int START_SEARCH = 0;
    private final static int ONE_ROUTE_COMPLETE = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case START_SEARCH:
                    if (!distanceManager.isUnsearchedEmpty()){
                        Distance dis = distanceManager.getUnsearchedDis();
                        routePlanResultListener.setDistance(dis);
                        LatLng s = dis.getS();
                        LatLng d = dis.getD();

                        BikingRoutePlanOption bikingOption = new BikingRoutePlanOption();
                        bikingOption.from(PlanNode.withLocation(s));//起点
                        bikingOption.to(PlanNode.withLocation(d));//终点
                        routePlanSearch.bikingSearch(bikingOption);//发起路线查询
                    }

                    break;
                case ONE_ROUTE_COMPLETE:
                    Distance dis = (Distance) msg.obj;
                    distanceManager.setInstance(dis);
                    if (!distanceManager.isUnsearchedEmpty()){
                        handler.sendEmptyMessage(START_SEARCH);
                    }else {
                        searchComplete();
                    }

                    break;
            }
        }
    };

    /**
     * 完成了所有路径搜索
     */
    private void searchComplete() {
//       distanceManager.showMatrix();
        Log.i(TAG,"开始计算.......");
        long start = System.nanoTime();
        int testCount = 1;
        int[] v = new int[matrixLen];
        float shortestPathValue = 0;
        List<Integer> result = null;
//        for (int i=0;i<testCount;i++){
//            TSPNearestNeighbour tspNearestNeighbour = new TSPNearestNeighbour();
//            result = tspNearestNeighbour.tsp(matrix,v);
//            shortestPathValue += result.get(result.size()-1);
//        }
        for (int i=0;i<testCount;i++){
            BBTSP bbtsp = new BBTSP(matrix);
            result = bbtsp.bbTsp(v);
            shortestPathValue += result.get(result.size()-1);
        }

        long end = System.nanoTime();
        long during = (end-start)/1000000;
        Log.i(TAG,"计算耗时:"+during+" ms");

        shortestPathValue = shortestPathValue/testCount;

        StringBuffer sb = new StringBuffer();
        sb.append("最短路程长为：").append(shortestPathValue).append("米\n");
        sb.append("最短路程计划：");

        Log.i(TAG,result.toString());

        v = new int[matrixLen-1];
        for (int i=0;i<result.size()-1;i++){
            v[i] = result.get(i);
            if (i == 0){
                sb.append("起点-->");
            }
            else {
                sb.append((result.get(i)-1)+"-->");
            }

        }
        sb.append("起点");

        sb.append("\n");
        Log.i(TAG,sb.toString());

        tvResult.setText(sb.toString());

        BikingRouteOverlay bikingRouteOverlay = new BikingRouteOverlay(map);
        bikingRouteOverlay.setData(distanceManager.getBikingLine(v));
        bikingRouteOverlay.addToMap();
        bikingRouteOverlay.zoomToSpan();

        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }


    int iconIndex = 0;
    /**
     * 地图长按监听
     */
    BaiduMap.OnMapLongClickListener onMapLongClickListener = new BaiduMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            iconIndex++;

            LatLng des = latLng;

            BitmapDescriptor icon = BitmapDescriptorFactory
                    .fromAssetWithDpi("mark"+iconIndex+".png");

            MarkerOptions markerOptions = new MarkerOptions().icon(icon).position(des);
            Marker marker = (Marker) map.addOverlay(markerOptions);
            markers.add(marker);

        }
    };


    /**
     * 百度地图路径规划结果回调,以及路线邻阶矩阵存储
     */
    class MyRoutePlanResultListener implements OnGetRoutePlanResultListener {


        Distance distance;

        public void setDistance(Distance distance){
            this.distance = distance;
        }


        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            if (bikingRouteResult == null
                    || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.i(TAG,"抱歉，未找到结果");
            }

            if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                Log.i(TAG,bikingRouteResult.getSuggestAddrInfo().toString());
                return;
            }

            if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {


                List<BikingRouteLine> lines = bikingRouteResult.getRouteLines();
                BikingRouteLine min = lines.get(0);
                for (BikingRouteLine l:lines){
                    if (l.getDistance() < min.getDistance()){
                        Log.i(TAG,"路径长："+l.getDistance()+"m");
                        min = l;
                    }
                }
                Log.i(TAG,"最短："+min.getDistance()+"m");

                Message msg = new Message();
                msg.what = ONE_ROUTE_COMPLETE;
                distance.setDistance( min.getDistance());
                distance.setSearched(true);
                distance.setLine(min);
                msg.obj = this.distance;
                handler.sendMessage(msg);

//                Log.i(TAG,"路线长："+min.getDistance()+" m");

            }
        }
    }


    private boolean ifFrist = true;
    private void navigateTo(BDLocation location) {
        // 按照经纬度确定地图位置
        if (ifFrist) {

            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
            // 移动到某经纬度
            map.setMapStatus(update);
            update = MapStatusUpdateFactory.zoomBy(5f);
            map.animateMapStatus(update);
            ifFrist = false;
        }
        // 显示个人位置图标
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData data = builder.build();
        myLocationData = data;
        map.setMyLocationData(data);
        locationService.stop();
    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Log.i(TAG,"获得位置");
                navigateTo(location);

//                StringBuffer sb = new StringBuffer(256);
//                sb.append("time : ");
//                /**
//                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//                 */
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ");// POI信息
//                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                    for (int i = 0; i < location.getPoiList().size(); i++) {
//                        Poi poi = (Poi) location.getPoiList().get(i);
//                        sb.append(poi.getName() + ";");
//                    }
//                }
//                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                    sb.append("\nspeed : ");
//                    sb.append(location.getSpeed());// 速度 单位：km/h
//                    sb.append("\nsatellite : ");
//                    sb.append(location.getSatelliteNumber());// 卫星数目
//                    sb.append("\nheight : ");
//                    sb.append(location.getAltitude());// 海拔高度 单位：米
//                    sb.append("\ngps status : ");
//                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//                    sb.append("\ndescribe : ");
//                    sb.append("gps定位成功");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                    // 运营商信息
//                    if (location.hasAltitude()) {// *****如果有海拔高度*****
//                        sb.append("\nheight : ");
//                        sb.append(location.getAltitude());// 单位：米
//                    }
//                    sb.append("\noperationers : ");// 运营商信息
//                    sb.append(location.getOperators());
//                    sb.append("\ndescribe : ");
//                    sb.append("网络定位成功");
//                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                    sb.append("\ndescribe : ");
//                    sb.append("离线定位成功，离线定位结果也是有效的");
//                } else if (location.getLocType() == BDLocation.TypeServerError) {
//                    sb.append("\ndescribe : ");
//                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
//                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//                }
//               Log.i(TAG,sb.toString());

//                locationService.stop();
            }
        }

    };
}
