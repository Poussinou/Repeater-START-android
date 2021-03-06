/**
 Repeater START - Showing The Amateur Repeaters Tool
 (C) 2020 Luke Bryan.
 This is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License
 as published by the Free Software Foundation; version 2.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package com.hearham.repeaterstart;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Main repeater listing with sortability.
 */
public class RepeaterListAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<JSONObject> data;
	private static LayoutInflater inflater = null;
	private LatLng center;

	public RepeaterListAdapter(Context context, ArrayList<JSONObject> data, final LatLng center)
	{
		this.context = context;
		this.data = data;
		this.center = center;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Collections.sort(this.data, new Comparator<JSONObject>()
		{
			@Override
			public int compare(JSONObject ob1, JSONObject ob2)
			{
				double d1 = 0;
				double d2 = 0;
				try {
					d1 = Utils.distance(ob1, center.getLatitude(), center.getLongitude());
					d2 = Utils.distance(ob2, center.getLatitude(), center.getLongitude());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (d1 > d2) return 1;
				if (d1 == d2) return 0;
				return -1;
			}
		});
	}

	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public Object getItem(int position)
	{
		return data.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View vi = convertView;
		if (vi == null)
			vi = inflater.inflate(R.layout.repeaterrow, null);
		TextView label1 = (TextView) vi.findViewById(R.id.label1);
		TextView label2 = (TextView) vi.findViewById(R.id.label2);
		TextView distlbl = (TextView) vi.findViewById(R.id.distlbl);
		try {
			JSONObject obj = data.get(position);
			String mhz = String.valueOf(obj.getDouble("frequency") / 1000000.0) + "mhz";
			String iNode = obj.getString("internet_node");
			if (null != iNode && iNode != "null") {
				label1.setText("Node " + iNode + ", " + obj.getString("callsign") + " at " + mhz);
			} else {
				label1.setText(obj.getString("callsign") + ", " + mhz);
			}
			if( obj.getInt("operational") < 1 ) {
				label1.setPaintFlags(label1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			}
			label2.setText("PL " + obj.getString("encode") + ", Offset " + String.valueOf(obj.getInt("offset") / 1000000.0) + ",\n" +
					data.get(position).getString("description"));

			double dist = Utils.distance(obj, center.getLatitude(), center.getLongitude());
			//To Miles TODO option for this
			dist = .62137119*dist;
			distlbl.setText(String.format("%.2g",dist)+"mi");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return vi;
	}

	public LatLng repeaterPos(int i) throws JSONException
	{
		LatLng position = new LatLng();
		position.setLatitude(data.get(i).getDouble("latitude"));
		position.setLongitude(data.get(i).getDouble("longitude"));
		return position;
	}
}