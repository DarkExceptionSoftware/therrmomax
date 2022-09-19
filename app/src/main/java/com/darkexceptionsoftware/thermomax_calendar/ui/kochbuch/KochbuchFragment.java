package com.darkexceptionsoftware.thermomax_calendar.ui.kochbuch;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeDates;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeModel;
import static com.darkexceptionsoftware.thermomax_calendar.MainActivity.setOnSelectedListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.DateModel;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.data.if_RecycleViewOnClickListener;
import com.darkexceptionsoftware.thermomax_calendar.data.UserDao;
import com.darkexceptionsoftware.thermomax_calendar.data.if_action_bar_access;
import com.darkexceptionsoftware.thermomax_calendar.data.fetch_recipes;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentKochbuchBinding;
import com.darkexceptionsoftware.thermomax_calendar.popup.ContextMenu_kochbuch;
import com.darkexceptionsoftware.thermomax_calendar.popup.Detail;
import com.darkexceptionsoftware.thermomax_calendar.popup.NewRecipe;
import com.darkexceptionsoftware.thermomax_calendar.web.Jsoup_parse;
import com.darkexceptionsoftware.thermomax_calendar.web.WebViewClass;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class KochbuchFragment extends Fragment implements if_RecycleViewOnClickListener, if_action_bar_access, SwipeRefreshLayout.OnRefreshListener {

    View root;
    RecipeModel recipe;
    String RecDir;
    private FragmentKochbuchBinding binding;
    private SharedPreferences prefs;
    private Activity activityReference;
    private RecyclerView recyclerView;
    private RecycleViewAdapter_Recipe rva;
    private fetch_recipes fr;
    private SwipeRefreshLayout swipeLayout;
    private MainActivity ref;
    private boolean stop = false;
    private int blink = 0;
    private Handler handler;
    private Runnable runnable;

    public KochbuchFragment() {
    }

    public RecycleViewAdapter_Recipe getRva() {
        return rva;
    }

    public void setRva(RecycleViewAdapter_Recipe rva) {
        this.rva = rva;
    }

    public ArrayList<RecipeModel> getRecipes() {
        return _RecipeModel;
    }

    public void setRecipes(ArrayList<RecipeModel> recipes) {
        _RecipeModel = recipes;
    }




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        KochbuchViewModel galleryViewModel =
                new ViewModelProvider(this).get(KochbuchViewModel.class);
        activityReference = getActivity();

        ref = (MainActivity) activityReference;
        ref.setmMainLayout(R.menu.kochbuch_main);
        ref.invalidateOptionsMenu();

        binding = FragmentKochbuchBinding.inflate(inflater, container, false);
        RecDir = activityReference.getApplicationContext().getApplicationInfo().dataDir + "/files/";

        root = binding.getRoot();

        swipeLayout = binding.swipeContainer;
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setRefreshing(false);

        _RecipeModel = ref.get_RecipeModel();

        setOnSelectedListener(this);


        // galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        recyclerView = binding.rcvCookbook;
        rva = new RecycleViewAdapter_Recipe(this, getContext(), RecycleViewAdapter_Recipe.ViewMode.RECIPEBOOK, this);
        recyclerView.setAdapter(rva);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rva.getSmoothScroller().setTargetPosition(MainActivity.get_rv_today_position());
        recyclerView.getLayoutManager().startSmoothScroll(rva.getSmoothScroller());

        binding.textView3.setText(R.string.tip_cb);
        binding.tipIcon.setVisibility(View.VISIBLE);
        binding.tipIcon2.setVisibility(View.GONE);
        int c = rva.getItemCount();


        if (c > 0) {
            MainActivity.change_appbar_icons(R.drawable.icon_post_add);
            binding.tipCard.setVisibility(View.GONE);
        } else {
            MainActivity.change_appbar_icons(R.drawable.img_indrigent_bottle);
            binding.tipCard.setVisibility(View.VISIBLE);
        }


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int delay = 1000;
                if (_RecipeModel.size() > 0)
                    stop = true;

                if (blink == 0) {
                    MainActivity.change_appbar_icons(R.drawable.icon_post_add);

                    blink = 1;
                    delay = 1000;

                } else {
                    MainActivity.change_appbar_icons(R.drawable.icon_post_add_notice);
                    blink = 0;
                    delay = 1000;
                }

                if (!stop) {
                    handler.postDelayed(this, delay);
                } else {
                    MainActivity.change_appbar_icons(R.drawable.icon_post_add);

                }
            }
        };

        stop = false;
        handler.postDelayed(runnable, 900);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stop = true;

        binding = null;
    }

    @Override
    public void clicked_button(int id) {

        Intent intent;

        switch (id){
            case R.id.menu_new_recipe:
                intent = new Intent(activityReference, NewRecipe.class);
                intent.putExtra("action", "newRecipe");
                startActivityForResult(intent, 1);
                break;
            case R.id.menu_fetch_ck:
                intent = new Intent(activityReference, WebViewClass.class);
                // TextView editText = (TextView) findViewById(R.id.confirm_label);
                intent.putExtra("action", "findWeb");
                intent.putExtra("url", "https:///www.chefkoch.de/");
                startActivityForResult(intent, 1);
                break;
            case R.id.menu_recipe_3:
                intent = new Intent(activityReference, WebViewClass.class);
                intent.putExtra("action", "findWeb");
                intent.putExtra("url", "file:///android_asset/index.html");
                startActivityForResult(intent, 1);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        int position = -1;
        String action = "";
        String url = "about:blank";
        try {
            Bundle extras = data.getExtras();


            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                if (extras != null) {
                    action = extras.getString("action");
                }
            }

            if (action.equals("edit")) {
                position = extras.getInt("pos");
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        _RecipeModel.clear();
                        fr = new fetch_recipes(activityReference, rva, _RecipeModel);
                        fr.execute();
                        CardView tipCard = (CardView) root.findViewById(R.id.tip_card);
                        int c = rva.getItemCount();
                        tipCard.setVisibility(View.GONE);
                        stop = true;
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 1000);
            }

            if (action.equals("parseany")) {

                url = extras.getString("result");
                String html = extras.getString("html");

                Intent intent;
                intent = new Intent(activityReference, NewRecipe.class);
                intent.putExtra("action", "parseany");
                intent.putExtra("result",url);
                intent.putExtra("html",html);
                startActivityForResult(intent, 1);

            }

            if (action.equals("tobrowser")){
                url = extras.getString("url");

                Intent intent = new Intent(activityReference, WebViewClass.class);
                intent.putExtra("action", "findWeb");
                intent.putExtra("url", url);
                startActivityForResult(intent, 1);
            }
            if (action.equals("findWeb")) {

                url = extras.getString("result");

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        _RecipeModel.clear();
                        fr = new fetch_recipes(activityReference, rva, _RecipeModel);
                        fr.execute();
                        CardView tipCard = (CardView) root.findViewById(R.id.tip_card);
                        int c = rva.getItemCount();
                        tipCard.setVisibility(View.GONE);
                        stop = true;
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 1000);

            }

            if (action.equals("appointment")) {
                position = extras.getInt("pos");
                long date = extras.getLong("date");
                UserDao userDao = ref.db.userDao();

                DateModel new_date = new DateModel(_RecipeModel.get(position).getId(), date, "self", 0, 0);
                try {
                    userDao.insertAll(new_date);

                } catch (Exception e) {
                    Writer writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));
                    String s = writer.toString();

                    Log.d("DAO", s);
                }

                _RecipeDates.add(new_date);

                DateFormat now = new SimpleDateFormat("dd-MM-yyyy");

                DateModel todelete = null;
                boolean foundTodayRecipes = false;
                for (DateModel temp : _RecipeDates) {
                    if (now.format(temp.getDate()).equals(now.format(new Date())) && !temp.getUser().equals("Thermomax Info"))
                        foundTodayRecipes = true;

                    if (temp.getUser().equals("Thermomax Info"))
                        todelete = temp;
                }
                if (todelete != null)
                    _RecipeDates.remove(todelete);

                if (!foundTodayRecipes)
                    _RecipeDates.add(new DateModel(new Long(0), new Date().getTime(), "Fritz", -1, 0));


                _RecipeDates.sort(Comparator.comparing(DateModel::getDate));


            }

            if (action.equals("question")) {
                position = extras.getInt("pos");
                if (position != -1)
                    parseUrl(url);
            }

            if (action.equals("delete")) {
                position = extras.getInt("pos");

                recipe = _RecipeModel.get(position);
                recipe.delete(RecDir, recipe.getId() + ".rcp");
                _RecipeModel.remove(position);

                rva.notifyItemRemoved(position);
                MainActivity.change_appbar_icons(R.drawable.icon_post_add);



                CardView tipCard = (CardView) root.findViewById(R.id.tip_card);

                if (_RecipeModel.size() < 1) {
                    tipCard.setVisibility(View.VISIBLE);
                    stop = false;
                    handler.postDelayed(runnable, 500);


                }
            }
        } catch (Exception e) {

        }
    }


    public void parseUrl(String url) {
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


//                Jparse.setTargetAdress(url);
                Jparse.setReturnReference(activityReference);
                Jparse.execute();
                //Jparse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemlongClick(int position, String action) {
        Intent intent = new Intent(getContext(), ContextMenu_kochbuch.class);
        // TextView editText = (TextView) findViewById(R.id.confirm_label);
        intent.putExtra("action", "show");
        intent.putExtra("pos", position);
        intent.putExtra("edit", 1);

        intent.putExtra("info", (Parcelable) _RecipeModel.get(position));
        startActivityForResult(intent, 1);
    }

    @Override
    public void onViewClick(View view, String action) {

    }


    @Override
    public void onItemClick(int position, String action) {
        if (action.equals("show")) {
            // confirm.showPopupWindow(activityReference.getCurrentFocus());


            Intent intent = new Intent(getContext(), Detail.class);
            // TextView editText = (TextView) findViewById(R.id.confirm_label);
            intent.putExtra("action", "show");
            intent.putExtra("pos", position);
            intent.putExtra("info", (Parcelable) _RecipeModel.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(false);

        Runnable r = () -> {
            fr = new fetch_recipes(activityReference, rva, _RecipeModel);
            fr.execute();
        };

        Handler h = new Handler();
        swipeLayout.post(r);
    }
}