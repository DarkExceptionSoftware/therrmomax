package com.darkexceptionsoftware.thermomax_calendar.ui.sevenDays;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.setOnSelectedListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.if_RecycleViewOnClickListener;
import com.darkexceptionsoftware.thermomax_calendar.data.if_action_bar_access;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentKochbuchBinding;
import com.darkexceptionsoftware.thermomax_calendar.ui.kochbuch.RecycleViewAdapter_Recipe;

public class sevenDaysFragment extends Fragment implements if_RecycleViewOnClickListener, if_action_bar_access {

    private FragmentKochbuchBinding binding;
    private Activity activityReference;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private MainActivity ref;

    private RecycleViewAdapter_Recipe rva;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sevenDaysViewModel sevenDaysViewModel =
                new ViewModelProvider(this).get(sevenDaysViewModel.class);

        activityReference = getActivity();
        ref = (MainActivity) activityReference;


        binding = FragmentKochbuchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setOnSelectedListener((if_action_bar_access) this);

        swipeLayout = binding.swipeContainer;
        // swipeLayout.setOnRefreshListener(this);
        swipeLayout.setEnabled(false);



        recyclerView = binding.rcvCookbook;
        rva = new RecycleViewAdapter_Recipe(this, getContext(), RecycleViewAdapter_Recipe.ViewMode.WEEKLIST, (if_RecycleViewOnClickListener) this);
        recyclerView.setAdapter(rva);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rva.getSmoothScroller().setTargetPosition(MainActivity.get_rv_today_position());
        recyclerView.getLayoutManager().startSmoothScroll(rva.getSmoothScroller());


        // fr = new fetch_recipes(activityReference, rva, _RecipeModel);
        // fr.execute();

        binding.textView3.setText(R.string.tip_7days);
        binding.tipIcon.setVisibility(View.VISIBLE);
        binding.tipIcon2.setVisibility(View.VISIBLE);
        binding.tipIcon.setImageResource(R.drawable.post_book);
        binding.tipIcon2.setImageResource(R.drawable.post_menu);


        if (rva.getItemCount() > 0) {
            binding.tipCard.setVisibility(View.GONE);
        }else{
            binding.tipCard.setVisibility(View.VISIBLE);
        }

        ref.setActionbuttonstate(false);
        ref.invalidateOptionsMenu();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int delay = 1000;
                if (rva.getItemCount() > 0)
                    stop = true;

                if (blink == 0){
                    MainActivity.change_navbar_icons(R.drawable.icon_menu);
                    MainActivity.change_drawer_color(false);

                    blink = 1;
                    delay = 1000;

                }else{
                    MainActivity.change_navbar_icons(R.drawable.icon_menu_notice);
                    MainActivity.change_drawer_color(true);
                    blink = 0;
                    delay = 1000;
                }

                if (!stop){
                    handler.postDelayed(this, delay);
                }else{
                    MainActivity.change_navbar_icons(R.drawable.icon_menu);
                    MainActivity.change_drawer_color(false);

                }
            }
        };

        stop = false;
        handler.postDelayed(runnable, 1000);

        return root;
    }

    private Handler handler;
    private Runnable runnable;
    private Boolean stop = false;
    private int blink = 0;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stop = true;
        binding = null;
    }

    @Override
    public void onItemClick(int position, String action) {
        Toast.makeText(getContext(),"1", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemlongClick(int position, String action) {
        Toast.makeText(getContext(),"2", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onViewClick(View view, String action) {

    }

    @Override
    public void clickedHomebutton() {
        Toast.makeText(getContext(),"3", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clickedAddbutton() {
        Toast.makeText(getContext(),"4", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clickedFab1() {
        Toast.makeText(getContext(),"5", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clickedFab2() {
        Toast.makeText(getContext(),"6", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clicked_m1_Button() {

    }

    @Override
    public void clicked_m3_Button() {

    }

    @Override
    public void clicked_m2_Button() {

    }
}