package com.darkexceptionsoftware.thermomax_calendar.ui.sevenDays;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeModel;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.setOnSelectedListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.data.RecycleViewOnClickListener;
import com.darkexceptionsoftware.thermomax_calendar.data.action_bar_access;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentKochbuchBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentWebviewBinding;
import com.darkexceptionsoftware.thermomax_calendar.ui.kochbuch.RecycleViewAdapter_Recipe;

public class sevenDaysFragment extends Fragment implements RecycleViewOnClickListener, action_bar_access{

    private FragmentKochbuchBinding binding;
    private Activity activityReference;
    private RecyclerView recyclerView;
    private RecycleViewAdapter_Recipe rva;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sevenDaysViewModel sevenDaysViewModel =
                new ViewModelProvider(this).get(sevenDaysViewModel.class);

        activityReference = getActivity();


        binding = FragmentKochbuchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setOnSelectedListener((action_bar_access) this);


        recyclerView = binding.rcvCookbook;
        rva = new RecycleViewAdapter_Recipe(this, getContext(), RecycleViewAdapter_Recipe.ViewMode.WEEKLIST, (RecycleViewOnClickListener) this);
        recyclerView.setAdapter(rva);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rva.getSmoothScroller().setTargetPosition(MainActivity.get_rv_today_position());
        recyclerView.getLayoutManager().startSmoothScroll(rva.getSmoothScroller());


        // fr = new fetch_recipes(activityReference, rva, _RecipeModel);
        // fr.execute();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
}