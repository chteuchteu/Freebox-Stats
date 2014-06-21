package com.chteuchteu.freeboxstats.hlpr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GraphHelper {
	public static Number[] jsonArrayToData(String field, JSONArray dataArray) throws JSONException {
		Number[] res = new Number[dataArray.length()];
		for (int i=0; i<dataArray.length(); i++) {
			JSONObject jsonObj = dataArray.getJSONObject(i);
			res[i] = jsonObj.getInt(field);
		}
		
		return res;
	}
}