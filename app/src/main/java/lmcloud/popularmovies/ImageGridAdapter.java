package lmcloud.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


// Reference: https://futurestud.io/blog/picasso-adapter-use-for-listview-gridview-etc/
// Modified from the ImageListAdapter
public class ImageGridAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private static final String TAG = ImageGridAdapter.class.getSimpleName();

    private ArrayList<mParcel> mList;

    public ImageGridAdapter(Context context, ArrayList<mParcel> mList) {
        super(context, R.layout.m_image, mList);
        this.context = context;
        this.mList = mList;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.m_image, parent, false);
        }
        try {
            Picasso
                    .with(context)
                    .load(mList.get(position).mPosterPath)
                    .into((ImageView) convertView);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Malformed/Missing URL", e);
        }
        return convertView;
    }

    public ArrayList<mParcel> getMovieList() {
        return this.mList;
    }
}