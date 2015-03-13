package NewHarnessRest;

import java.util.Iterator;

import org.json.JSONObject;

public abstract class RESTService {
	
	protected JSONObject rawData = new JSONObject();
	
	public RESTService(){		
		
	}
	
	public void refreshData(JSONObject o){				
		this.rawData = o;		
	}
	
	protected String serviceString;
	
	public String getServiceString(){
		return serviceString;
	}
	
	private boolean needSSID = false;
	
	public boolean isCookieNeeded(){
		return needSSID;
	}
	
	
	abstract public JSONObject parseLeafJSONData(JSONObject response, String httpMethod);
	abstract public String generateURL();
	abstract public JSONObject generatePayload();
}
