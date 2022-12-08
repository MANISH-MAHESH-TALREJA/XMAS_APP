package com.vpapps.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.vpapps.adapter.AdapterWallHome;
import com.vpapps.adapter.HomePagerAdapter;
import com.vpapps.asyncTask.LoadWallpapers;
import net.manish.christmas.MainActivity;
import net.manish.christmas.R;
import net.manish.christmas.SingleWallpaper;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.interfaces.WallpaperListener;
import com.vpapps.item.ItemWallpaper;
import com.vpapps.util.Constant;
import com.vpapps.util.Methods;
import com.vpapps.util.RecyclerItemClickListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentHome extends Fragment {

    private Methods methods;
    private RecyclerView recyclerView;
    private ArrayList<ItemWallpaper> arrayList_wallpaper, arrayList_mostviewed;
    private EnchantedViewPager enchantedViewPager;
    private AppCompatButton button_wall;

    private CircularProgressBar progressBar;
    private NestedScrollView scrollView;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Constant.arrayList_wallpaper.clear();
                Constant.arrayList_wallpaper.addAll(arrayList_mostviewed);
                Intent intent = new Intent(getActivity(), SingleWallpaper.class);
                intent.putExtra("pos", position);
                startActivity(intent);
            }
        });
        arrayList_mostviewed = new ArrayList<>();
        arrayList_wallpaper = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.progressBar_home);
        enchantedViewPager = rootView.findViewById(R.id.viewPager_home);
        enchantedViewPager.useScale();

        button_wall = rootView.findViewById(R.id.button_more_wall);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_data_found);
        scrollView = rootView.findViewById(R.id.scrollView);

        recyclerView = rootView.findViewById(R.id.recyclerView_home_wall);
        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(glm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, "");
            }
        }));

        button_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentMostViewWallpaper f1 = new FragmentMostViewWallpaper();
                FragmentTransaction ft = fm.beginTransaction();

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(fm.getFragments().get(fm.getFragments().size() - 1));
                ft.add(R.id.frame_nav, f1, getString(R.string.most_view_wallpaper));
                ft.addToBackStack(getString(R.string.most_view_wallpaper));
                ft.commit();

                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.most_view_wallpaper));
            }
        });

        loadData();

        return rootView;
    }

    private void loadData() {
        if (methods.isNetworkAvailable()) {
            LoadWallpapers loadWallpapers = new LoadWallpapers(new WallpaperListener() {
                @Override
                public void onStart() {
                    arrayList_wallpaper.clear();
                    ll_empty.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWallPapers) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            arrayList_wallpaper.addAll(arrayListWallPapers);
                            errr_msg = getString(R.string.no_wallpaper_found);
                        } else {
                            errr_msg = getString(R.string.server_no_conn);
                        }

                        loadMostViewed();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_WALLPAPER, 0, "", "", ""));

            loadWallpapers.execute();
        } else {
            errr_msg = getString(R.string.net_not_conn);
            setEmpty();
        }
    }

    private void loadMostViewed() {
        if (methods.isNetworkAvailable()) {
            LoadWallpapers loadWallpapers = new LoadWallpapers(new WallpaperListener() {
                @Override
                public void onStart() {
                    arrayList_mostviewed.clear();
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWallPapers) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                errr_msg = getString(R.string.no_wallpaper_found);
                                arrayList_mostviewed.addAll(arrayListWallPapers);
                                setAdapter();
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            errr_msg = getString(R.string.server_no_conn);
                            setEmpty();
                        }
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_MOST_VIEWED_WALLPAPER, 0, "", "", ""));

            loadWallpapers.execute();
        } else {
            errr_msg = getString(R.string.net_not_conn);
            setEmpty();
        }

    }


    private void setAdapter() {
        int columnWidth = (methods.getScreenWidth() - ((2 + 1))) / 2;
        AdapterWallHome adapterWallHome = new AdapterWallHome(arrayList_mostviewed, columnWidth - 50);
        recyclerView.setAdapter(adapterWallHome);

        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(getActivity(), arrayList_wallpaper);
        enchantedViewPager.setAdapter(homePagerAdapter);
        setEmpty();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList_wallpaper.size() > 0) {
            scrollView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            scrollView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }
}