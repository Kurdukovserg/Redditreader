package com.example.redditreader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class SimpleAdapterExtended extends SimpleAdapter {
    private Context parent;
    private List<? extends Map<String, ?>> data;

    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public SimpleAdapterExtended(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.parent = context;
        this.data = data;
    }

    @Override
    public void setViewImage(ImageView v, String value) {
            try {
                v.setImageResource(Integer.parseInt(value));
            } catch (NumberFormatException nfe) {
                v.setImageURI(Uri.parse(value));
                v.setClickable(true);
                v.setFocusable(true);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url;
                        String indexof = value.substring(value.lastIndexOf("img")+3, value.indexOf("."));
                        try {
                            int index = Integer.parseInt(indexof);
                            url = String.valueOf(data.get(index).get("url"));
                        }catch (NumberFormatException e){
                            url = indexof;
                        }
                        Toast.makeText(parent.getApplicationContext(), "clicked:"+url, Toast.LENGTH_SHORT).show();
                        Intent i2=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        parent.startActivity(i2);
                    }
                });
            }
        }

}
