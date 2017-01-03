package com.example.guanzhuli.icart.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.guanzhuli.icart.R;
import com.example.guanzhuli.icart.data.Adapters.CategoryAdapter;
import com.example.guanzhuli.icart.data.Category;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.guanzhuli.icart.Fragment.CategoryFragment.CATEGORY_ID_KEY;

public class SubCategoryFragment extends Fragment {
    private RequestQueue mRequestQueue;
    public static final String SUB_CATEGORY_URL =
            "http://rjtmobile.com/ansari/shopingcart/androidapp/cust_sub_category.php?Id=";
    public static final String SUBCATEGORY_ID_KEY = "subcategoryID";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRequestQueue = Volley.newRequestQueue(getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sub_category, container, false);
        String url = null;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String categoryID = bundle.getString(CATEGORY_ID_KEY, "-1");
            if (categoryID.equals("-1")) {
                Toast.makeText(getContext(), "category request fail", Toast.LENGTH_SHORT).show();
                return view;
            }
            url = SUB_CATEGORY_URL + categoryID;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        List<Category> categoryList = new ArrayList<>();
                        try {
                            JSONArray categories = new JSONObject(s).getJSONArray("SubCategory");
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject item = categories.getJSONObject(i);
                                String name = item.getString("SubCatagoryName");
                                String imageUrl = item.getString("CatagoryImage");
                                String id = item.getString("Id");
                                categoryList.add(new Category(imageUrl, name, id));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final CategoryAdapter adapter = new CategoryAdapter(getContext(), R.layout.category_listview_item, categoryList);
                        ListView listView = (ListView) getView().findViewById(R.id.sub_category_list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Category category = (Category) adapterView.getItemAtPosition(i);
                                ItemListFragment itemListFragment = new ItemListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString(SUBCATEGORY_ID_KEY, category.getId());
                                itemListFragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.main_fragment_container, itemListFragment)
                                        .addToBackStack(SubCategoryFragment.class.getName())
                                        .commit();

                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), "network error!", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(stringRequest);
        return view;
    }

}