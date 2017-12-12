package com.facec.cksalstl.MediaReview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.facec.cksalstl.MediaReview.adapter.MovieListAdapter;
import com.facec.cksalstl.MediaReview.common.HttpClient;
import com.facec.cksalstl.MediaReview.common.URLRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {
    private Map<String, Object> map = null;
    private ListView listView = null;
    private MovieListAdapter adapter = null;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    private String search = null;
    private String nextPageToken = null;
    boolean lastItemVisibleFlag = false;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);

        Bundle args = getArguments();
        search = args.getString("search");

        adapter = new MovieListAdapter(getContext(), R.layout.movie_list_view, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), YouTubePlayActivity.class);
                intent.putExtra("videoId", (String) list.get(position).get("videoId"));
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    appendListData(nextPageToken);
                }
            }

        });

        appendListData(nextPageToken);

        return rootView;
    }

    public void appendListData(final String nextPageToken) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://www.googleapis.com/youtube/v3/search";
                URLRequest req = new URLRequest(url);
                req.put("key", "AIzaSyCCBYTwSooSoyhnavrtQj3mWiC9bPJl-4w");
                req.put("part", "snippet");
                req.put("type", "video");
                req.put("maxResults", "20");
                req.put("order", "relevance");
                req.put("q", search);

                if(nextPageToken != null && !nextPageToken.trim().equals("")) {
                    req.put("pageToken", nextPageToken);
                }

                HttpClient client = new HttpClient();
                String result = client.get(req);
                if(result != null) {
                    ObjectMapper om = new ObjectMapper();
                    try {
                        map = om.readValue(result, new TypeReference<Map<String, Object>>() {});

                        MainFragment.this.nextPageToken = (String) map.get("nextPageToken");

                        List<Map<String, Object>> items = (List<Map<String, Object>>) map.get("items");
                        for(int i = 0; i < items.size(); i++) {
                            Map<String, Object> item = items.get(i);

                            Map<String, Object> id = (Map<String, Object>) item.get("id");
                            String videoId = (String) id.get("videoId");

                            Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                            String title = (String) snippet.get("title");
                            String date = (String) snippet.get("publishedAt");
                            date = date.substring(0, date.indexOf("T"));

                            Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                            Map<String, Object> def = (Map<String, Object>) thumbnails.get("default");
                            String imgUrl = (String) def.get("url");

                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("videoId", videoId);
                            map.put("title", title);
                            map.put("date", date);
                            map.put("thumbnails", imgUrl);

                            list.add(map);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }

            }
        }).start();
    }
}
