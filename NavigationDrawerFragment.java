package com.facec.cksalstl.MediaReview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.facec.cksalstl.MediaReview.adapter.NavigationDrawerMenuListAdapter;

import java.util.List;
import java.util.Map;

public class NavigationDrawerFragment extends Fragment {
    private FrameLayout navigationDrawer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        navigationDrawer = (FrameLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        return navigationDrawer;
    }

    public void addList(List<Map<String, Object>> list) {
        TextView title = (TextView) navigationDrawer.findViewById(R.id.title);
        title.setText("리뷰 리스트 (" + list.size() + ")");

        ListView listView = (ListView) navigationDrawer.findViewById(R.id.list_view);
        NavigationDrawerMenuListAdapter adapter = new NavigationDrawerMenuListAdapter(getContext(), R.layout.navigation_drawer_menu_list_view, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity act = (MainActivity)getContext();
                act.moveTabPosition(position);
                act.closeDrawer();
            }
        });
    }
}