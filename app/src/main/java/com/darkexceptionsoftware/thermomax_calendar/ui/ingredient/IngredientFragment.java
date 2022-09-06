package com.darkexceptionsoftware.thermomax_calendar.ui.ingredient;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._Einkaufsliste;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.db_ig;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.setOnSelectedListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.DateModel;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.IndrigentModel;
import com.darkexceptionsoftware.thermomax_calendar.data.IndrigentParser;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.data.if_RecycleViewOnClickListener;
import com.darkexceptionsoftware.thermomax_calendar.data.UserDao_indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.if_action_bar_access;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentIngredientBinding;
import com.darkexceptionsoftware.thermomax_calendar.popup.ContextMenu_einkaufsliste;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class IngredientFragment extends Fragment implements if_RecycleViewOnClickListener, if_action_bar_access {

    private FragmentIngredientBinding binding;
    private SharedPreferences prefs;
    private Activity activityReference;
    private RecyclerView recyclerView;
    private RecycleViewAdapter_Ingredient rva;
    private MainActivity ref;

    private int if_mode = 0;
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
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

    }


    @Override
    public void onItemClick(int position, String action) {
        UserDao_indrigent userDao_indrigent = MainActivity.db_ig.userDao();

        if (if_mode == 2) {
            String name = _Einkaufsliste.get(position).getName();
            int type = _Einkaufsliste.get(position).getSortof();
            userDao_indrigent.deleteByName(name);
            clickedAddbutton();

        }

    }

    @Override
    public void onItemlongClick(int position, String action) {
        if (if_mode == 1) {

            Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), ContextMenu_einkaufsliste.class);
            intent.putExtra("action", "show");
            intent.putExtra("name", _Einkaufsliste.get(position).getName());
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        int cat = -1;
        String name = "";
        String url = "about:blank";
        try {
            Bundle extras = data.getExtras();


            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                if (extras != null) {
                    String action = extras.getString("action", "none");

                    if (action.equals("cat")) {
                        name = extras.getString("indname", "none");
                        cat = extras.getInt("category", 0);
                        db_ig.userDao().insertAll(new IndrigentModel(name.toUpperCase(Locale.ROOT).trim(), cat));
                    }
                }
            }
        } catch (Exception e) {

        }

        Toast.makeText(getContext(), name + " = " + cat, Toast.LENGTH_SHORT).show();

        clickedHomebutton();
    }

    @Override
    public void clickedHomebutton() {
        if_mode = 1;
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
        IndrigentParser ip = MainActivity.getIndrigentParser();
        il.addAll(ip.outputList_db(iResult));
        rva.notifyDataSetChanged();
        lastView = null;
    }

    @Override
    public void clickedAddbutton() {
        if_mode = 2;

        Toast.makeText(getContext(), "5", Toast.LENGTH_SHORT).show();
        UserDao_indrigent userDao_indrigent = MainActivity.db_ig.userDao();

        List<DateModel> dl = MainActivity.get_RecipeDates_week();
        List<Indrigent> il = MainActivity.get_Einkaufsliste();
        List<Indrigent> iResult = new ArrayList<>();

        il.clear();
        List<IndrigentModel> rml = (ArrayList<IndrigentModel>) userDao_indrigent.getAll();

        for (IndrigentModel item : rml) {
            il.add(new Indrigent(0f, "", item.getName_de(), item.getType()));
        }
        il.sort(Comparator.comparing(Indrigent::getName));
        il.sort(Comparator.comparing(Indrigent::getSortof));


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