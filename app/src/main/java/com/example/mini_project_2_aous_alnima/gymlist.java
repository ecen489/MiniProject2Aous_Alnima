package com.example.mini_project_2_aous_alnima;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class gymlist extends ArrayAdapter<gym> {


        private Activity context;
        private List<gym> gymList;

        public gymlist(Activity context, List<gym> gymList) {
            super(context, R.layout.layout_list, gymList);
            this.context = context;
            this.gymList = gymList;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_list, null, true);

            TextView textView = (TextView) listViewItem.findViewById(R.id.textView);
            gym gymvisit = gymList.get(position);
            textView.setText(gymvisit.getTimeIn());
            return listViewItem;
        }
}





