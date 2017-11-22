package uur.com.pinbook.JavaFiles;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uur.com.pinbook.R;

/**
 * Created by mac on 21.11.2017.
 */

public class CustomPagerAdapter extends PagerAdapter{

    private List<EnterPageDataModel> itemList;
    private Context context;

    private LayoutInflater layoutInflater;

    public CustomPagerAdapter(Context context, List<EnterPageDataModel> itemList) {

        this.context = context;
        this.itemList = itemList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = layoutInflater.inflate(R.layout.tek_satir, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_item);
        TextView tv = (TextView) view.findViewById(R.id.textView);

        EnterPageDataModel tempDataModel = itemList.get(position);

        imageView.setImageResource(tempDataModel.getImageID());
        tv.setText(tempDataModel.getTitle());

        container.addView(view);
        return  view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);

    }
}
