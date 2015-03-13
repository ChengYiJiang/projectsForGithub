package NewHarnessRest;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class RESTServiceFactory {

	HashMap<String, RESTService> serviceMap;

	public RESTServiceFactory() {
		this.serviceMap = new HashMap<String, RESTService>();
		// adding the services
		serviceMap.put("Item", new itemService());
		serviceMap.put("Dataport", new dataportsService());
		serviceMap.put("Location", new locationService());
		serviceMap.put("Ip", new ipService());
		serviceMap.put("Ipassignments", new IpassignmentsService());
	}

	// service object refresh every time before a step is passed in
	public RESTService getService(String serviceName, JSONObject rawData) {
		RESTService output = serviceMap.get(serviceName);
		output.refreshData(rawData);
		return output;
		//abcd
	//asdfsddfsdf 
	}
}

// external api for item CRUD, cannot search, key param is the item ID
class itemService extends RESTService {

	public itemService() {
		super();
		this.serviceString = "item";
		System.out.println("item service set up");
	}

	@Override
	public String generateURL() {
		// TODO Auto-generated method stub
		String temp = "items";
		try {
			if (rawData.has("id") && !rawData.get("id").equals(""))
				temp += "/" + rawData.get("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	@Override
	public JSONObject generatePayload() {
		try {
			return rawData.getJSONObject("rawData");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject parseLeafJSONData(JSONObject response, String httpMethod) {
		try {
			if (!httpMethod.equals("DELETE")) {
				return response.getJSONObject("item");

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
}

// external rest api for dataports CRUD, it runs as a child of item and besides
// item ID the dataport ID may also be required
class dataportsService extends RESTService {

	public dataportsService() {
		super();
		this.serviceString = "dataport";
		System.out.println("dataport service set up");
	}

	@Override
	public String generateURL() {
		// TODO Auto-generated method stub
		String temp = "items";
		try {
			if (rawData.has("id") && !rawData.get("id").equals(""))
				temp += "/" + rawData.get("id") + "/dataports";
			if (rawData.has("portID") && !rawData.get("portID").equals(""))
				temp += "/" + rawData.get("portID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return temp;
	}

	@Override
	public JSONObject generatePayload() {
		try {
			return rawData.getJSONObject("rawData");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject parseLeafJSONData(JSONObject response, String httpMethod) {
		try {
			if (!httpMethod.equals("DELETE")) {
				return response.getJSONObject("dataport");
			} else if (httpMethod.equals("GET")) {
				System.out.println("GET");
				System.out.println(response);
				return response;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONObject();

	}
}

// location service external api, only update is supported
class locationService extends RESTService {

	public locationService() {
		super();
		this.serviceString = "location";
		System.out.println("location service set up");
	}

	@Override
	public String generateURL() {
		// TODO Auto-generated method stub
		String temp = "location";
		try {
			if (rawData.has("id") && !rawData.get("id").equals(""))
				temp += "/" + rawData.get("id"); // this id is location id
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	@Override
	public JSONObject generatePayload() {
		try {
			return rawData.getJSONObject("rawData");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject parseLeafJSONData(JSONObject response, String httpMethod) {
		try {
			if (!httpMethod.equals("GET")) {
				return response.getJSONObject("location");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
}

class ipService extends RESTService {

	public ipService() {
		super();
		this.serviceString = "ipaddresses";
		System.out.println("ip service set up");
	}

	@Override
	public JSONObject parseLeafJSONData(JSONObject response, String httpMethod) {
		if (httpMethod.equals("GET")) {
			return response;
		}
		return new JSONObject();
	}

	@Override
	public String generateURL() {
		String temp = "ip/ipaddresses";
		try {
			// these four options may need more work like check null and
			// priority setting
			if (rawData.has("ipAddress") && rawData.has("locationId"))
				temp += "?ipAddress=" + rawData.get("ipAddress")
						+ "&locationId=" + rawData.get("locationId");
			if (rawData.has("id") && !rawData.get("id").equals(""))
				temp += "?id=" + rawData.get("id"); // this id is location id
			if (rawData.has("itemId") && !rawData.get("itemId").equals(""))
				temp += "?itemId=" + rawData.get("itemId");
			if (rawData.has("subnetId") && !rawData.get("subnetId").equals(""))
				temp += "/availableIps?subnetId=" + rawData.get("subnetId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	@Override
	public JSONObject generatePayload() {
		try {
			return rawData.getJSONObject("rawData");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

class IpassignmentsService extends RESTService {
	
	
	public IpassignmentsService() {
		super();
		this.serviceString = "ipassignments";
		System.out.println("ipassignments service set up");
	}
	

	@Override
	public JSONObject parseLeafJSONData(JSONObject response, String httpMethod) {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public String generateURL() {
		String temp = "ip/ipassignments";
		try {
			if (rawData.get("Method").toString().equals("Ipassignments_Retrieve")){
				temp += "?itemId=" + rawData.get("itemId");
				if (rawData.has("locationId") && !rawData.getString("locationId").equals(""))
					temp += "&locationId=" + rawData.get("locationId");
			}
			else if (rawData.get("Method").toString().equals("Ipassignments_Create")){
				temp += "?createAs=" + rawData.get("createAs") + "&isGateway=" + rawData.get("isGateway");
			} 
			else if (rawData.get("Method").toString().equals("Ipassignments_Delete")){
				temp += "/" + rawData.get("id");
			} 
			else if (rawData.get("Method").toString().equals("Ipassignments_Update")){
				temp += "?createAs=" + rawData.get("createAs") + "&isGateway=" + rawData.get("isGateway");
			}
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		return temp;
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
