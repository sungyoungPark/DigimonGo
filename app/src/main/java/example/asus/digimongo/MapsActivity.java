package example.asus.digimongo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    // 구글 API 연결은 내 위치에 대한 정보 얻기위해서 구글에서 제공하는 보다 쉬운 방법 - FusedLocationProviderClient
    private GoogleApiClient mGoogleApiClient; // GoogleApiClient.ConnectionCallbacks , OnConnectionFailedListener 달게 됨.
    private FusedLocationProviderClient mFusedLocationClient;
    private ToggleButton b_mapmode, b_showCurPos;
    static public int REQUEST_CODE_PERMISSIONS = 1000; // REQUESTPermission에 대한

    Location myLocate;

    float a, b; // 위도와 경도
    LatLng hansung = new LatLng(37.582022, 127.009866);
    double random_a, random_b;
    double alpha, beta;
    MarkerOptions mk;
    Marker myMarker;
    CircleOptions circle;

    double longitude = 0;
    double latitude = 0;
    double altitude;
    LatLng myPos;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    //Context context = ;

    boolean item_flag = true;
    BroadcastReceiver mReceiver;
    int powerUP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        pref = getSharedPreferences(getString(R.string.info), this.MODE_PRIVATE);  //sharedpreferences를 통해 구글맵에서 얻은 아이템 저장
        editor = pref.edit();

        myLocate = new Location("myLocate");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
        ////////////////////////////////// 권한 요청 끝

        ////////////////////////////////// GoogleAPIClient로 놀기~
        // GoogleAPIClient의 인스턴스 생성
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // 현재 위치 정보를 얻을 수 있는 객체

        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ServiceFinish");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                powerUP=intent.getIntExtra("finish",1);
                Log.d("4449","맵 액티비티에서 아이템 값:"+powerUP);
                editor.putInt("item", powerUP);
                editor.commit();
                Log.d("3268","service 끝남:"+powerUP);
            }
        };
        registerReceiver(mReceiver, intentFilter);
    }

    // 권한 체크 다이얼로그에 대해 거부됬을때 승낙됬을 때
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 승인될 경우
                } else {
                    // 권한이 거부될 경우
                    Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && // 여기까진 위치정보 권한 잘 설정되어있는지
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {  // 여기까진 위치정보 권한 잘 설정되어있는지
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, // 권한 얻기위한 대화상자 표시
                    REQUEST_CODE_PERMISSIONS);
            return;
        }

        // 내 위치 파악하기 !!!!
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myPos = new LatLng(location.getLatitude(), location.getLongitude()); // 현재위치 (latitude : 위도 / longitude : 경도)
                    Log.d("현재위치는 ", location.getLatitude() + "," + location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mylocationimage);  //나의 위치 마커 이미지 변경
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    myMarker = mMap.addMarker(markerOptions.position(myPos).title("내 위치"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                }
            }
        });

        // 마커 클릭한 순간의 이벤트 <-> OninfoWindowClickListener (마커 정보창을 클릭한 순간의 이벤트)
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Location point = new Location("point");
                point.setLatitude(marker.getPosition().latitude);
                point.setLongitude(marker.getPosition().longitude);
                float distance = myLocate.distanceTo(point);
                Log.d("4449", "" + distance + "");
                if (distance > 5 && distance < 50000000) {    //원래는 50
                    powerUP = (int) (Math.random() * 5) + 1;  //1~5까지 랜덤으로 파워업 지정
                    editor.putInt("item", powerUP);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "아이템 얻음", Toast.LENGTH_LONG).show();
                    marker.remove();
                    Log.d("4449", "파워업 :" + powerUP);
                    item_flag = true;
                    Intent intent = new Intent(MapsActivity.this, powerUPService.class);
                    startService(intent);
                } else
                    Toast.makeText(getApplicationContext(), "더 가까이가세요", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        mMap.getUiSettings().setAllGesturesEnabled(false);

    }

    private final LocationListener mLocationListener = new LocationListener() {   //실시간 위치 갱신된 위도 경도 보내줌
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            myLocate = location;
            Log.d("test", "onLocationChanged, location:" + location);
            double longitude1 = location.getLongitude(); //경도
            double latitude1 = location.getLatitude();   //위도
            altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            Log.d("3838", longitude1 + "/" + latitude1);
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            if (longitude != longitude || latitude != latitude1) {
                changeLoc(latitude1, longitude1, myPos);
            }

        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    public void changeLoc(double latitude, double longitude, LatLng myPos) {  //내 위치가 변할때마다 마커 이동
        if (myMarker != null) {
            myMarker.remove();
        }
        myPos = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mylocationimage);  //나의 위치 마커 이미지 변경
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        myMarker = mMap.addMarker(markerOptions.position(myPos).title("내 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }

    public void SearchTreasure_btn_click(View view) {
        if (item_flag) {
            // 보물 랜덤 생성
            BitmapDrawable bitmap_draw = (BitmapDrawable) getResources().getDrawable(R.drawable.itemloactionimage);
            Bitmap bit = bitmap_draw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(bit, 100, 100, false);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);
            int rand = new Random().nextInt(4); // 0~3사이
            Log.d("random값", rand + " ");

            // 알파 베타 = 내 위치 기준으로 4사분면 얼마큼씩 (+) 되서 생성될껀지
            switch (rand) {
                case 0:
                    alpha = ((double) (new Random().nextInt(1000))) / 1000000; // 0.000100 ~ 0.000999
                    beta = ((double) (new Random().nextInt(1000))) / 1000000; // 0.000100 ~ 0.000999
                    break;

                case 1:
                    alpha = ((double) (new Random().nextInt(1000))) / 1000000; // 0.000100 ~ 0.000999
                    beta = -((double) (new Random().nextInt(1000))) / 1000000; // -0.000100 ~ -0.000999
                    break;

                case 2:
                    alpha = -((double) (new Random().nextInt(1000))) / 1000000; // - 0.000100 ~ -0.000999
                    beta = -((double) (new Random().nextInt(1000))) / 1000000; // - 0.000100 ~ -0.000999
                    break;
                case 3:
                    alpha = -((double) (new Random().nextInt(1000))) / 1000000; // -0.000100 ~ -0.000999
                    beta = ((double) (new Random().nextInt(1000))) / 1000000;  // 0.000100 ~ 0.000999
                    break;
            }
            Log.d("알파베타", alpha + "," + beta);

            // 보물의 최종위치
            random_a = myPos.latitude + alpha;
            random_b = myPos.longitude + beta;

            mk = new MarkerOptions()
                    .position(new LatLng(random_a, random_b)) // 마커의 위치
                    .icon(bitmapDescriptor) // 이미지
                    .title("보물");
            mMap.addMarker(mk);
            Log.d("보물위치", random_a + "," + random_b);
            item_flag = false;
        }
    }

    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);

        return distance;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unregisterReceiver(mReceiver);
    }
}
