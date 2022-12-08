package com.vpapps.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter;
import net.manish.christmas.R;
import net.manish.christmas.SingleWallpaper;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.item.ItemWallpaper;
import com.vpapps.util.Constant;
import com.vpapps.util.Methods;

import java.util.ArrayList;

public class HomePagerAdapter extends EnchantedViewPagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<ItemWallpaper> arrayList;
    private Methods methods;

    public HomePagerAdapter(Context context, ArrayList<ItemWallpaper> arrayList) {
        super(arrayList);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = arrayList;
        methods = new Methods(context, interAdListener);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View mCurrentView = inflater.inflate(R.layout.layout_home_pager, container, false);

        RoundedImageView imageView = mCurrentView.findViewById(R.id.imageView_home_vp);

        Picasso.get()
                .load(arrayList.get(position).getImageBig())
                .resize(650,450)
                .placeholder(R.mipmap.app_icon)
                .into(imageView);

        mCurrentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterAd(position, "");
            }
        });

        mCurrentView.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        container.addView(mCurrentView);

        return mCurrentView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 5;
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constant.arrayList_wallpaper.clear();
            Constant.arrayList_wallpaper.addAll(arrayList);
            Intent intent = new Intent(mContext, SingleWallpaper.class);
            intent.putExtra("pos", position);
            mContext.startActivity(intent);
        }
    };
}