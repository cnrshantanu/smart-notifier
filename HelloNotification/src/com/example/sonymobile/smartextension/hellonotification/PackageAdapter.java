package com.example.sonymobile.smartextension.hellonotification;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zakoi.accessory.smartnotify.receiver.PackageDataModel;

public class PackageAdapter extends ArrayAdapter<PackageDataModel> {

	private List<PackageDataModel> m_packageList = new ArrayList<PackageDataModel>();
	private int m_resource;
	private Context m_context;

	public PackageAdapter(Context context, int resource,
			List<PackageDataModel> objects) {

		super(context, resource, objects);
		m_packageList = objects;
		m_resource = resource;
		m_context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = convertView;

		if (itemView == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			itemView = vi.inflate(m_resource, parent, false);
		}

		PackageDataModel currentPackage = m_packageList.get(position);
		TextView textview = (TextView) itemView.findViewById(R.id.AppName);
		textview.setText(currentPackage.getAppName());
		ImageView imageview = (ImageView) itemView.findViewById(R.id.icon);
		try {
			ApplicationInfo appInfo = m_context.getPackageManager().getApplicationInfo(currentPackage.getPackage(),0);
			Drawable d = appInfo.loadIcon(m_context.getPackageManager());
			imageview.setImageDrawable(d);
		} 
		catch(Exception e) {
			
		}
		
		//imageview.setImageResource(R.drawable.);

		return itemView;
	}

}
