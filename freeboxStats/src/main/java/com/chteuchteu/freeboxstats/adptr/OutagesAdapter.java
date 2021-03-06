package com.chteuchteu.freeboxstats.adptr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chteuchteu.freeboxstats.R;
import com.chteuchteu.freeboxstats.obj.Outage;

import java.util.List;

public class OutagesAdapter extends ArrayAdapter<Outage> {
	private Context context;
	
	public OutagesAdapter(Context context, List<Outage> items) {
		super(context, R.layout.outage_item, items);
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = LayoutInflater.from(context);
			v = vi.inflate(R.layout.outage_item, parent, false);
		}
		
		Outage o = getItem(position);
		
		if (o != null) {
			TextView tv_duration = (TextView) v.findViewById(R.id.outage_duration);
			tv_duration.setText(o.getDurationString(context));
			
			if (o.isSingleDay()) {
				v.findViewById(R.id.outage_fromto_severaldays).setVisibility(View.GONE);
				TextView tv_date = (TextView) v.findViewById(R.id.outage_date_oneday);
				TextView tv_from_hour = (TextView) v.findViewById(R.id.outage_from_hour_oneday);
				TextView tv_to_hour = (TextView) v.findViewById(R.id.outage_to_hour_oneday);
				
				tv_date.setText(o.getFromDateString());
				tv_from_hour.setText(o.getFromHourString());
				tv_to_hour.setText(o.getToHourString());
			} else {
				v.findViewById(R.id.outage_fromto_oneday).setVisibility(View.GONE);
				TextView tv_from_date = (TextView) v.findViewById(R.id.outage_from_date);
				TextView tv_from_hour = (TextView) v.findViewById(R.id.outage_from_hour);
				TextView tv_to_date = (TextView) v.findViewById(R.id.outage_to_date);
				TextView tv_to_hour = (TextView) v.findViewById(R.id.outage_to_hour);
				
				tv_from_date.setText(o.getFromDateString());
				tv_from_hour.setText(o.getFromHourString());
				tv_to_date.setText(o.getToDateString());
				tv_to_hour.setText(o.getToHourString());
			}
		}
		
		return v;
	}
}
