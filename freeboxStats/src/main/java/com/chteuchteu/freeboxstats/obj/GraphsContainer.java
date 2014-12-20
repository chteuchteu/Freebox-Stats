package com.chteuchteu.freeboxstats.obj;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chteuchteu.freeboxstats.hlpr.Enums.Field;
import com.chteuchteu.freeboxstats.hlpr.Enums.FieldType;
import com.chteuchteu.freeboxstats.hlpr.Enums.Period;
import com.chteuchteu.freeboxstats.hlpr.Enums.Unit;
import com.chteuchteu.freeboxstats.hlpr.GraphHelper;
import com.chteuchteu.freeboxstats.hlpr.SettingsHelper;

public class GraphsContainer {
	protected ArrayList<String> serie;
	protected ArrayList<DataSet> dataSets;
	protected Period period;
	public static final Period defaultPeriod = Period.HOUR;
	protected Unit valuesUnit;
	public static final Unit defaultUnit = Unit.Mo;
	private FieldType fieldType;
	public static final FieldType defaultFieldType = FieldType.DATA;
	
	// Since StackGraphsContainer extends GraphsContainer
	public GraphsContainer(ArrayList<Field> fields, JSONArray data, Period period) { }
	
	public GraphsContainer(ArrayList<Field> fields, JSONArray data, FieldType fieldType, Period period) {
		// Construct GraphsContainer from raw data from the Freebox
		this.period = period;
		this.serie = new ArrayList<String>();
		this.dataSets = new ArrayList<DataSet>();
		if (fieldType == FieldType.DATA)
			this.valuesUnit = defaultUnit;
		else if (fieldType == FieldType.TEMP)
			this.valuesUnit = Unit.C;
		else if (fieldType == FieldType.NOISE)
			this.valuesUnit = Unit.dB;
		this.fieldType = fieldType;
		
		for (Field f : fields)
			this.dataSets.add(new DataSet(f, valuesUnit));
		
		computeData(data, fields);
	}
	
	private void computeData(JSONArray data, ArrayList<Field> fields) {
		int timestampDiff = 0;
		try {
			timestampDiff = GraphHelper.getTimestampDiff(data);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		
		// Adjust precision
		switch (SettingsHelper.getInstance().getGraphPrecision()) {
			case Max:
				// Do nothing
				break;
			case Medium:
				timestampDiff = timestampDiff*3;
				break;
			case Min:
				timestampDiff = timestampDiff*4;
				break;
			default:
				break;
		}
		
		int lastAddedTimestamp = -1;
		ValuesBuffer valuesBuffer = new ValuesBuffer(fields);
		for (int i=0; i<data.length(); i++) {
			try {
				JSONObject obj = (JSONObject) data.get(i);
				
				if (lastAddedTimestamp == -1)
					lastAddedTimestamp = obj.getInt("time");
				
				if (obj.getInt("time") - lastAddedTimestamp >= timestampDiff
						|| obj.getInt("time") - lastAddedTimestamp == 0
						|| timestampDiff == 0) {
					// Add the Numbers to the values lists
					for (Field f : fields) {
						try {
							int val = 0;
							if (obj.has(f.getSerializedValue()))
								val = obj.getInt(f.getSerializedValue());
							
							if (!valuesBuffer.isEmpty()) { // Include all the buffered values
								valuesBuffer.addValue(f, val);
								val = valuesBuffer.getAverage(f);
								valuesBuffer.clear(f);
							}
							
							if (fieldType == FieldType.DATA)
								getDataSet(f).addValue(Unit.o, val);
							else if (fieldType == FieldType.TEMP)
								getDataSet(f).addValue(Unit.C, val);
							else if (fieldType == FieldType.NOISE)
								getDataSet(f).addValue(Unit.dB, val);
						} catch (JSONException ex) { ex.printStackTrace(); }
					}
					lastAddedTimestamp = obj.getInt("time");
					
					this.serie.add(GraphHelper.getDateLabelFromTimestamp(obj.getLong("time"), this.period));
				} else {
					for (Field f : fields) {
						try {
							int val = 0;
							if (obj.has(f.getSerializedValue()))
								val = obj.getInt(f.getSerializedValue());
							valuesBuffer.addValue(f, val);
						} catch (JSONException ex) { ex.printStackTrace(); }
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		// Get the best values unit
		if (fieldType == FieldType.DATA) {
			int highestValue = GraphHelper.getHighestValue(data, fields);
			Unit bestUnit = GraphHelper.getBestUnitByMaxVal(highestValue);
			
			if (bestUnit != defaultUnit) {
				convertAllValues(defaultUnit, bestUnit);
				this.valuesUnit = bestUnit;
			}
		}
	}
	
	protected void convertAllValues(Unit from, Unit to) {
		if (this.fieldType == FieldType.TEMP)
			return;
		
		for (DataSet ds : this.dataSets)
			ds.setValuesUnit(to, true);
	}
	
	public void setSerie(ArrayList<String> val) { this.serie = val; }
	public ArrayList<String> getSerie() { return this.serie; }
	
	public void addDataSet(DataSet val) { this.dataSets.add(val); }
	public void addDataSet(Field field, JSONArray jsonArray, Unit valuesUnit) { this.dataSets.add(new DataSet(field, jsonArray, valuesUnit)); }
	public DataSet getDataSet(Field field) {
		for (DataSet ds : this.dataSets) {
			if (ds.getField().equals(field))
				return ds;
		}
		return null;
	}
	public ArrayList<DataSet> getDataSets() { return this.dataSets; }
	public ArrayList<Field> getFields() {
		ArrayList<Field> fields = new ArrayList<Field>();
		for (DataSet ds : this.dataSets)
			fields.add(ds.getField());
		return fields;
	}
	public Unit getValuesUnit() { return this.valuesUnit; }
}