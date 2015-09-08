package com.android.message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Button;

public class LocationServ{
	
public class CellIDInfo {
		
		public int cellId;
		public String mobileCountryCode;
		public String mobileNetworkCode;
		public int locationAreaCode;
		public String radioType;
		public CellIDInfo(){}
	}

	private CdmaCellLocation location = null;
	private Button btnGetInfo = null; 
	private static final int TYPE = 1;
	
	Context mContext;
	
	double latitude;  //经度
	double longitude; //纬度
	public LocationServ(Context context) {
		mContext = context;
		latitude = 0;
		longitude = 0;
		
	}
	public LocationServ() {
		latitude = 0;
		longitude = 0;
	}
	
	//获得经纬度  Format: @经度@纬度
	public String getLocation() {
		/*
		*/
		
	
		int count = 0;
		
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		int type = tm.getNetworkType();
		//在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA，电信的3G为EVDO 
		//String OperatorName = tm.getNetworkOperatorName(); 
		Location loc = null;
		ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
		//中国电信为CTC
		//NETWORK_TYPE_EVDO_A是中国电信3G的getNetworkType
		//NETWORK_TYPE_CDMA电信2G是CDMA
		if (type == TelephonyManager.NETWORK_TYPE_EVDO_A || type == TelephonyManager.NETWORK_TYPE_CDMA || type ==TelephonyManager.NETWORK_TYPE_1xRTT)
		{
			location = (CdmaCellLocation) tm.getCellLocation();
			int cellIDs = location.getBaseStationId();
			int networkID = location.getNetworkId();
			StringBuilder nsb = new StringBuilder();
			nsb.append(location.getSystemId());
            CellIDInfo info = new CellIDInfo();
            info.cellId = cellIDs;
            info.locationAreaCode = networkID; //ok
            info.mobileNetworkCode = nsb.toString();
            info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
            info.radioType = "cdma";
            CellID.add(info);
		}
		//移动2G卡 + CMCC + 2 
		//type = NETWORK_TYPE_EDGE
		else if(type == TelephonyManager.NETWORK_TYPE_EDGE)
		{
			GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();  
			int cellIDs = location.getCid();  
			int lac = location.getLac(); 
			CellIDInfo info = new CellIDInfo();
            info.cellId = cellIDs;
            info.locationAreaCode = lac;
            info.mobileNetworkCode = tm.getNetworkOperator().substring(3, 5);   
            info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
            info.radioType = "gsm";
            CellID.add(info);
		}
		//联通的2G经过测试 China Unicom   1 NETWORK_TYPE_GPRS
		else if(type == TelephonyManager.NETWORK_TYPE_GPRS)
		{
			GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();  
			int cellIDs = location.getCid();  
			int lac = location.getLac(); 
			CellIDInfo info = new CellIDInfo();
            info.cellId = cellIDs;
            info.locationAreaCode = lac;
            info.radioType = "gsm";
            CellID.add(info);
		} else {
			WriteLogFile.writeLog("not support network");
			return oldGetLoc();
		}
		
		loc = callGear(CellID);
		
		if(loc != null) {
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();
			
			return "@" + new Double(longitude).toString() + "@" + new Double(latitude).toString();
		} else {
			WriteLogFile.writeLog("loc is null");
			return oldGetLoc();
		}
		
	}
	
	private Location callGear(ArrayList<CellIDInfo> cellID) {
    	if (cellID == null) return null;
    	DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://www.google.com/loc/json");
		JSONObject holder = new JSONObject();
		try {
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("home_mobile_country_code", cellID.get(0).mobileCountryCode);
			holder.put("home_mobile_network_code", cellID.get(0).mobileNetworkCode);
			holder.put("radio_type", cellID.get(0).radioType);
			holder.put("request_address", true);
			if ("460".equals(cellID.get(0).mobileCountryCode)) 
				holder.put("address_language", "zh_CN");
			else
				holder.put("address_language", "en_US");
			JSONObject data,current_data;
			JSONArray array = new JSONArray();
			current_data = new JSONObject();
			current_data.put("cell_id", cellID.get(0).cellId);
			current_data.put("location_area_code", cellID.get(0).locationAreaCode);
			current_data.put("mobile_country_code", cellID.get(0).mobileCountryCode);
			current_data.put("mobile_network_code", cellID.get(0).mobileNetworkCode);
			current_data.put("age", 0);
			array.put(current_data);
			if (cellID.size() > 2) {
				for (int i = 1; i < cellID.size(); i++) {
					data = new JSONObject();
					data.put("cell_id", cellID.get(i).cellId);
					data.put("location_area_code", cellID.get(i).locationAreaCode);
					data.put("mobile_country_code", cellID.get(i).mobileCountryCode);
					data.put("mobile_network_code", cellID.get(i).mobileNetworkCode);
					data.put("age", 0);
					array.put(data);
				}
			}
			holder.put("cell_towers", array);
			StringEntity se = new StringEntity(holder.toString());
			Log.e("Location send", holder.toString());
			post.setEntity(se);
			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();

			BufferedReader br = new BufferedReader(
					new InputStreamReader(entity.getContent()));
			StringBuffer sb = new StringBuffer();
			String result = br.readLine();
			while (result != null) {
				Log.e("Locaiton receive", result);
				sb.append(result);
				result = br.readLine();
			}
			if(sb.length() <= 1)
				return null;
			data = new JSONObject(sb.toString());
			data = (JSONObject) data.get("location");

			Location loc = new Location(LocationManager.NETWORK_PROVIDER);
			loc.setLatitude((Double) data.get("latitude"));
			loc.setLongitude((Double) data.get("longitude"));
			loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
			loc.setTime(GetUTCTime());
			return loc;
		} catch (JSONException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public long GetUTCTime() { 
        Calendar cal = Calendar.getInstance(Locale.CHINA); 
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET); 
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET); 
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset)); 
        return cal.getTimeInMillis();
    }
    
    public String oldGetLoc() {
    	try {
			WriteLogFile.writeLog("Begin to get old location");
			LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
			Log.v("Location Service---", "Generate manager");
			Location location = null;
			int count = 0;
			while(location == null && count < 10) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 1000,
						new LocationListener(){
						    public void onLocationChanged(Location location){
						        //更新位置
						    	WriteLogFile.writeLog("New Location==" +  location.getLongitude() + " " + location.getLatitude());
		
						    }
		
							public void onProviderDisabled(String provider) {
								// TODO Auto-generated method stub
								
							}
		
							public void onProviderEnabled(String provider) {
								// TODO Auto-generated method stub
								
							}
		
							public void onStatusChanged(String provider, int status, Bundle extras) {
								// TODO Auto-generated method stub
								
							}
		
						});
				//location = locationManager.getCurrentLoation();
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				Log.i("Location service", "getting location" + "---Try time" + count);
				++count;
			}
			if (location != null) {
				latitude = location.getLatitude(); // 经度
				longitude = location.getLongitude(); // 纬度
			}
			
			
			String ret = "@";
			ret += new Double(longitude).toString();
			ret += "@";
			ret += new Double(latitude).toString();
			Log.v("Location Service", ret);
			return ret;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "@0@0";
    }
}
