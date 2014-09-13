package com.chteuchteu.freeboxstats.obj;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chteuchteu.freeboxstats.hlpr.Enums.Field;
import com.chteuchteu.freeboxstats.hlpr.Enums.Unit;
import com.chteuchteu.freeboxstats.hlpr.Util;

public class DataSet {
	private Field field;
	private Unit valuesUnit;
	private ArrayList<Number> values;
	
	public DataSet(Field field, Unit valuesUnit) {
		this.field = field;
		this.values = new ArrayList<Number>();
		this.valuesUnit = valuesUnit;
	}
	
	public DataSet(Field field, JSONArray jsonArray, Unit valuesUnit) {
		this.valuesUnit = valuesUnit;
		this.values = new ArrayList<Number>();
		try {
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				
				if (jsonObj.has(field.getSerializedValue())) {
					int intValue = jsonObj.getInt(field.getSerializedValue());
					Number value = Util.convertUnit(Unit.o, valuesUnit, intValue);
					this.values.add(value);
				}
				else
					this.values.add(0);
			}
		} catch (JSONException e) { e.printStackTrace(); }
	}
	
	public Field getField() { return this.field; }
	/**
	 * Get this DataSet values.
	 * This shouldn't be used to add values, use addValue(Unit, int) insted
	 * @return
	 */
	public ArrayList<Number> getValues() { return this.values; }
	
	public void addValue(Unit unit, int value) {
		if (unit == Unit.C)
			this.values.add(value/10);
		else if (unit == Unit.dB)
			this.values.add((double)value/10);
		else
			this.values.add(Util.convertUnit(unit, valuesUnit, value));
	}
	
	public void stackValue(long val) {
		Number value = Util.convertUnit(Unit.o, valuesUnit, val);
		
		// val = (val-1)+val
		if (this.values.isEmpty())
			this.values.add(value);
		else {
			Number previousVal = this.values.get(this.values.size()-1);
			value = previousVal.longValue() + value.longValue();
			this.values.add(value);
		}
	}
	
	public void setValuesUnit(Unit unit, boolean convertAll) {
		if (convertAll) {
			ArrayList<Number> newValues = new ArrayList<Number>();
			for (Number number : this.values)
				newValues.add(Util.convertUnit(valuesUnit, unit, number.doubleValue()));
			this.values = newValues;
		}
		this.valuesUnit = unit;
	}
	public Unit getValuesUnit() { return this.valuesUnit; }
}