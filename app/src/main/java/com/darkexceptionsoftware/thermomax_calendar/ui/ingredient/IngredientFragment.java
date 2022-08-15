package com.darkexceptionsoftware.thermomax_calendar.ui.ingredient;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeDates;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.db;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.setOnSelectedListener;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.DateModel;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.IndrigentParser;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.data.RecycleViewOnClickListener;
import com.darkexceptionsoftware.thermomax_calendar.data.UserDao;
import com.darkexceptionsoftware.thermomax_calendar.data.Zutat;
import com.darkexceptionsoftware.thermomax_calendar.data.action_bar_access;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentHomeBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentIngredientBinding;
import com.darkexceptionsoftware.thermomax_calendar.popup.Confirm;
import com.darkexceptionsoftware.thermomax_calendar.popup.ContextMenu_Kalender;
import com.darkexceptionsoftware.thermomax_calendar.ui.home.HomeViewModel;
import com.darkexceptionsoftware.thermomax_calendar.ui.home.RecycleViewAdapter_RecipeDate;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class IngredientFragment extends Fragment implements RecycleViewOnClickListener, action_bar_access {

    private FragmentIngredientBinding binding;
    private SharedPreferences prefs;
    private Activity activityReference;
    private RecyclerView recyclerView;
    private RecycleViewAdapter_Ingredient rva;
    private MainActivity ref;

    private int lastPosition = -1;
    private View mView, lastView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        activityReference = getActivity();
        ref = (MainActivity) activityReference;


        setOnSelectedListener(this);

        IngredientViewModel ingredientViewModel =
                new ViewModelProvider(this).get(IngredientViewModel.class);

        binding = FragmentIngredientBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        recyclerView = binding.rcvIngredient;
        rva = new RecycleViewAdapter_Ingredient(getActivity(), MainActivity._Einkaufsliste, this);
        recyclerView.setAdapter(rva);
        recyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
        rva.getSmoothScroller().setTargetPosition(MainActivity.get_rv_today_position());
        recyclerView.getLayoutManager().startSmoothScroll(rva.getSmoothScroller());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //if (lastView != null)
                //setButtons(lastView, View.GONE);

                //lastView = null;
            }
        });
        MainActivity.change_appbar_icons(R.drawable.refresh);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewClick(View v, String action) {
        Toast.makeText(getContext(), "List:", Toast.LENGTH_SHORT).show();

        if (v != lastView)
            setButtons(lastView, View.GONE);

        setButtons(v, View.VISIBLE);

        lastView = v;
    }

    public void setButtons(View v, int vis) {

        if (v != null) {
            Button rcv_ind_btn1 = v.findViewById(R.id.rcv_ind_btn1);
            Button rcv_ind_btn2 = v.findViewById(R.id.rcv_ind_btn2);
            Button rcv_ind_btn3 = v.findViewById(R.id.rcv_ind_btn3);
            rcv_ind_btn1.setVisibility(vis);
            rcv_ind_btn2.setVisibility(vis);
            rcv_ind_btn3.setVisibility(vis);
        }
    }


    @Override
    public void onItemClick(int position, String action) {

    }

    @Override
    public void onItemlongClick(int position, String action) {
        Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clickedHomebutton() {
        Toast.makeText(getContext(), "4", Toast.LENGTH_SHORT).show();
        List<Indrigent> il = MainActivity.get_Einkaufsliste();
        List<DateModel> dl = MainActivity.get_RecipeDates_week();
        HashMap rm = MainActivity.getRecipeMap();
        List<Indrigent> iResult = new ArrayList<>();

        for (DateModel ditem : dl) {
            RecipeModel r = (RecipeModel) rm.get(ditem.getModel());
            if (r != null)
                iResult.addAll(r.getINDRIGENTS());
        }
        iResult.sort(Comparator.comparing(Indrigent::getName));

        il.clear();
        il.addAll(iResult);
        rva.notifyDataSetChanged();
        lastView = null;
    }

    @Override
    public void clickedAddbutton() {
        Toast.makeText(getContext(), "5", Toast.LENGTH_SHORT).show();
        List<DateModel> dl = MainActivity.get_RecipeDates_week();
        List<Indrigent> il = MainActivity.get_Einkaufsliste();
        List<Indrigent> iResult = new ArrayList<>();

        HashMap rm = MainActivity.getRecipeMap();

        for (DateModel ditem : dl) {
            RecipeModel r = (RecipeModel) rm.get(ditem.getModel());
            if (r != null)
                iResult.addAll(r.getINDRIGENTS());
        }
        iResult.sort(Comparator.comparing(Indrigent::getName));

        il.clear();
        IndrigentParser ip = MainActivity.getIndrigentParser();
        il.addAll(ip.outputList(iResult));

        rva.notifyDataSetChanged();
        lastView = null;

    }

    @Override
    public void clickedFab1() {
        Toast.makeText(getContext(), "6", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clickedFab2() {
        Toast.makeText(getContext(), "7", Toast.LENGTH_SHORT).show();

    }
}