package com.darkexceptionsoftware.thermomax_calendar.ui.home;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeDates;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeModel;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.db;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.setOnSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.darkexceptionsoftware.thermomax_calendar.data.UserDao;
import com.darkexceptionsoftware.thermomax_calendar.data.action_bar_access;
import com.darkexceptionsoftware.thermomax_calendar.data.RecycleViewOnClickListener;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentHomeBinding;
import com.darkexceptionsoftware.thermomax_calendar.popup.Confirm;
import com.darkexceptionsoftware.thermomax_calendar.popup.ContextMenu_Kalender;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;

public class HomeFragment extends Fragment implements RecycleViewOnClickListener, action_bar_access {

    private FragmentHomeBinding binding;
    private SharedPreferences prefs;
    private Activity activityReference;
    private RecyclerView recyclerView;
    private RecycleViewAdapter_RecipeDate rva;
    private MainActivity ref;

    private View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        activityReference = getActivity();
        ref = (MainActivity) activityReference;


        setOnSelectedListener(this);

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        String AppKey = "com.darkexceptionsoftware.thermomax_calendar";
        prefs = getContext().getSharedPreferences(
                AppKey, Context.MODE_PRIVATE);
        Boolean HAS_READ_EULA = prefs.getBoolean("HAS_READ_EULA", false);


        if (!HAS_READ_EULA) {
            Intent intent = new Intent(activityReference, Confirm.class);
            // TextView editText = (TextView) findViewById(R.id.confirm_label);
            intent.putExtra("action", "contract");
            startActivityForResult(intent, 1);
        }

        UserDao userDao = db.userDao();

        _RecipeDates.sort(Comparator.comparing(DateModel::getDate));


        recyclerView = binding.mRecyclerView;
        rva = new RecycleViewAdapter_RecipeDate(getActivity(), _RecipeDates,this);
        recyclerView.setAdapter(rva);
        recyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
        rva.getSmoothScroller().setTargetPosition(MainActivity.get_rv_today_position());
        recyclerView.getLayoutManager().startSmoothScroll(rva.getSmoothScroller());

        MainActivity.change_appbar_icons(R.drawable.post_add);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position, String action) {
        DateFormat now = new SimpleDateFormat("dd.MM.yyyy");

        if (action.equals("delete")) {
            // confirm.showPopupWindow(activityReference.getCurrentFocus());


            Intent intent = new Intent(getContext(), Confirm.class);
            // TextView editText = (TextView) findViewById(R.id.confirm_label);
            intent.putExtra("action", "question");
            intent.putExtra("pos", position);
            intent.putExtra("info", "Möchtest du den Termin\\n" + _RecipeDates.get(position).getModel() + " vom " + now.format(_RecipeDates.get(position).getDate()) + "\nwirklich Löschen?");
            startActivityForResult(intent, 1);
        }

        if (action.equals("deleteDirectly")) {
            db.userDao().delete(_RecipeDates.get(position));
            _RecipeDates.remove(position);
            rva.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemlongClick(int position, String action) {
        Intent intent = new Intent(getContext(), ContextMenu_Kalender.class);
        intent.putExtra("action", "show");
        intent.putExtra("pos", position);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onViewClick(View view, String action) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        int position = -1;
        String action = "";
        Bundle extras = data.getExtras();

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (extras != null) {
                action = extras.getString("action");
            }
        }
        if (action.equals("question")){
            position = extras.getInt("pos");

            if (position != -1){

                //USERDAO HERE
                db.userDao().delete(_RecipeDates.get(position));

                _RecipeDates.remove(position);

                rva.notifyDataSetChanged();
                MainActivity.set_Rvtp(MainActivity.get_rv_today_position());
                Toast.makeText(getContext(), action + " " + position, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
            }
        }
        if (action.equals("contract")){
            prefs.edit().putBoolean("HAS_READ_EULA", true).apply();
        }

        if (action.equals("delete")){
            prefs.edit().putBoolean("HAS_READ_EULA", true).apply();
        }
    }

    @Override
    public void clickedHomebutton() {
        // recyclerView.getLayoutManager().scrollToPosition(rv_today_position);
        rva.getSmoothScroller().setTargetPosition(MainActivity.rv_today_position);
        recyclerView.getLayoutManager().startSmoothScroll(rva.getSmoothScroller());


        Toast.makeText(getContext(), "Focusing today", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clickedAddbutton() {

        MainActivity.get_RecipeDates().clear();
        db.userDao().delete();
        rva.notifyDataSetChanged();
    }

    @Override
    public void clickedFab1() {
        Snackbar.make(getView(), "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void clickedFab2() {

    }
}