package com.darkexceptionsoftware.thermomax_calendar.ui.kochbuch;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeDates;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.setOnSelectedListener;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.data.RecycleViewAdapter_Recipe;
import com.darkexceptionsoftware.thermomax_calendar.data.RecycleViewAdapter_RecipeDate;
import com.darkexceptionsoftware.thermomax_calendar.data.RecycleViewOnClickListener;
import com.darkexceptionsoftware.thermomax_calendar.data.action_bar_access;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentHomeBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentKochbuchBinding;
import com.darkexceptionsoftware.thermomax_calendar.popup.Confirm;
import com.darkexceptionsoftware.thermomax_calendar.popup.ContextMenu;
import com.darkexceptionsoftware.thermomax_calendar.popup.Detail;
import com.darkexceptionsoftware.thermomax_calendar.web.Jsoup_parse;
import com.darkexceptionsoftware.thermomax_calendar.web.WebViewClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KochbuchFragment extends Fragment implements RecycleViewOnClickListener, action_bar_access {

    private FragmentKochbuchBinding binding;
    private SharedPreferences prefs;
    private Activity activityReference;
    private RecyclerView recyclerView;
    private RecycleViewAdapter_Recipe rva;
    ArrayList<RecipeModel> Recipes;

    RecipeModel recipe;

    public KochbuchFragment() {
        Recipes = new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        KochbuchViewModel galleryViewModel =
                new ViewModelProvider(this).get(KochbuchViewModel.class);

        binding = FragmentKochbuchBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        activityReference = getActivity();

        Recipes.clear();

        try {
            fetch_recipes();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setOnSelectedListener(this);


        final TextView textView = binding.textUrl;
        // galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        recyclerView = binding.rcvCookbook;
        rva = new RecycleViewAdapter_Recipe(this,getContext(), Recipes,this);
        recyclerView.setAdapter(rva);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rva.getSmoothScroller().setTargetPosition(MainActivity.get_rv_today_position());
        recyclerView.getLayoutManager().startSmoothScroll(rva.getSmoothScroller());







        return root;
    }

    private void fetch_recipes() throws MalformedURLException, PackageManager.NameNotFoundException {
        final TextView textView = binding.textUrl;
        String RecDir = activityReference.getApplicationContext().getApplicationInfo().dataDir + "/files/";

        String result = "";
        File directory = new File(RecDir);
        List<String> files = Arrays.asList(directory.list());
        Collections.sort(files,Collections.reverseOrder());




        List<String> cache = new ArrayList();

        for (String recipeitem : files){
            recipe = new RecipeModel(activityReference.getApplicationContext());

            if (!recipeitem.contains(".rcp"))
                continue;

            Boolean success = recipe.deserialize(RecDir,recipeitem);

            if (success){
                Recipes.add(recipe);
            }else{
                recipe.delete(RecDir,recipeitem);

            }

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void clickedHomebutton() {

    }

    @Override
    public void clickedAddbutton() {
        Intent intent = new Intent(activityReference, WebViewClass.class);
        // TextView editText = (TextView) findViewById(R.id.confirm_label);
        intent.putExtra("action", "findWeb");
        intent.putExtra("url", "https:////www.chefkoch.de////");

        startActivityForResult(intent, 1);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void clickedFab1() {

    }

    @Override
    public void clickedFab2() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        int position = -1;
        String action = "";
        String url  = "about:blank";
        Bundle extras = data.getExtras();

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (extras != null) {
                action = extras.getString("action");
            }
        }
        if (action.equals("findWeb")){
            url = extras.getString("result");
            parseUrl(url);
        }

        if (action.equals("question")){
            position = extras.getInt("pos");
            if (position != -1)
                parseUrl(url);
        }

        if (action.equals("refresh")){

        }
    }


    public void parseUrl(String url)
    {
        Jsoup_parse Jparse;

        try {
            Jparse = new Jsoup_parse(activityReference);


            String tvUrl = url;

            String UrlRegEx = "http?s?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}/g";
            // https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)
            UrlRegEx = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

            if (!tvUrl.contains("http")) {
                tvUrl = "http://" + tvUrl;
            }

            if (tvUrl.matches(UrlRegEx)) {
                // Snackbar.make(view, "Trying to scrape...", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();


                Jparse.setTextView(binding.textUrl);
                Jparse.setTargetAdress(url);
                Jparse.setReturnReference(activityReference);
                Jparse.execute();
                //Jparse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemlongClick(int position, String action){
        Intent intent = new Intent(getContext(), ContextMenu.class);
        // TextView editText = (TextView) findViewById(R.id.confirm_label);
        intent.putExtra("action", "show");
        intent.putExtra("pos", position);
        intent.putExtra("info", (Parcelable) Recipes.get(position));
        startActivityForResult(intent, 1);
    }


    @Override
    public void onItemClick(int position, String action) {
        if (action.equals("show")) {
            // confirm.showPopupWindow(activityReference.getCurrentFocus());



            Intent intent = new Intent(getContext(), Detail.class);
            // TextView editText = (TextView) findViewById(R.id.confirm_label);
            intent.putExtra("action", "show");
            intent.putExtra("pos", position);
            intent.putExtra("info", (Parcelable) Recipes.get(position));
            startActivity(intent);
        }


    }
}