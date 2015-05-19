package NewHarnessRest;

import org.json.JSONException;
import org.json.JSONObject;

public class SmartChartService extends RESTService{
	
	public SmartChartService() {
		super();
		this.serviceString = "smart chart";
		System.out.println("Smart chart service set up");
	}

	@Override
	public JSONObject parseLeafJSONData(JSONObject response, String httpMethod) {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public String generateURL() {
		// TODO Auto-generated method stub
		return "measurements";
	}

	@Override
	public JSONObject generatePayload() {
		// TODO Auto-generated method stub
				try {
					return rawData.getJSONObject("rawData");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
	}

}
