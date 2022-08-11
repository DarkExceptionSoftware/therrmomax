package com.darkexceptionsoftware.thermomax_calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.darkexceptionsoftware.thermomax_calendar.data.AppDatabase;
import com.darkexceptionsoftware.thermomax_calendar.data.DateModel;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.data.UserDao;
import com.darkexceptionsoftware.thermomax_calendar.data.action_bar_access;
import com.darkexceptionsoftware.thermomax_calendar.data.fetch_recipes;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.darkexceptionsoftware.thermomax_calendar.databinding.ActivityMainBinding;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static action_bar_access rViewAccess;
    public static ArrayList<DateModel> _RecipeDates = new ArrayList<>();
    public static ArrayList<RecipeModel> _RecipeModel = new ArrayList<>();
    public  static int rv_today_position = 0;
    public static AppDatabase db;
    public static int color1;
    public static int color2;
    static action_bar_access mCallback;
    private static HashMap<Long,RecipeModel> RecipeMap=new HashMap<Long,RecipeModel>();
    public List<String> old_files;
    URL url;
    private SharedPreferences prefs;
    private fetch_recipes fr;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Activity activityReference;
    private Context mContext;

    public static HashMap<Long, RecipeModel> getRecipeMap() {
        return RecipeMap;
    }

    public void setRecipeMap(HashMap<Long, RecipeModel> recipeMap) {
        RecipeMap = recipeMap;
    }

    public static ArrayList<DateModel> get_RecipeDates() {
        return _RecipeDates;
    }

    public static ArrayList<DateModel> get_RecipeDates_week() {
        List week = load_week();
        ArrayList<DateModel> _RecipeDates_week = new ArrayList<>();
        for (DateModel item : _RecipeDates){
            if (week.contains(item.getStringDate()))
                _RecipeDates_week.add(item);
        }
        return _RecipeDates_week;
    }

    public static ArrayList<RecipeModel> get_RecipeModel() {
        return _RecipeModel;
    }

    public static int get_Rvtp() {
        return rv_today_position;
    }

    public static void set_Rvtp(int value){
        rv_today_position = value;
    }

    public static void setOnSelectedListener(action_bar_access listener){
        mCallback = listener;
    }

    public static List load_week(){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatDay = new SimpleDateFormat("EE");
        Date date;
        List Dates = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(System.currentTimeMillis());
        date = c.getTime();
        Dates.add(format1.format(date));

        for (int i = 0; i < 6; i++){
            c.add(Calendar.DATE, 1);
            date = c.getTime();
            Dates.add(format1.format(date));
        }
        return Dates;
    }

    public static int get_rv_today_position() {
        DateFormat now = new SimpleDateFormat("dd-MM-yyyy");

        DateFormat year = new SimpleDateFormat("yyyy");
        DateFormat month = new SimpleDateFormat("MMMM");

        int _switch = 0;
        int color = color2;

        String lastmonth = "";
        String lastyear = "";

        int position = 0;
        for (int i = 0; i < _RecipeDates.size(); i++) {
            Long temp_date = _RecipeDates.get(i).getDate();
            if (now.format(temp_date).equals(now.format(new Date())))
                position = i;

            if (!month.format(temp_date).equals(lastmonth) ||
                    month.format(temp_date).equals(lastmonth) && !year.format(temp_date).equals(lastyear)) {

                if (_switch == 0) {
                    _switch = 1;
                    color = color2;
                } else {
                    _switch = 0;
                    color = color1;
                }


                lastmonth = month.format(temp_date);
                lastyear = year.format(temp_date);
                // break;
            }
            _RecipeDates.get(i).setBackcolor(color);

            // Log.d("lapse", "V" + i + ": " + now.format(_RecipeDates.get(i).getDate()) + " " + _RecipeDates.get(i).getBackcolor());

        }
        return position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityReference = this;
        mContext = getApplicationContext();

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "recipes").allowMainThreadQueries().build();

        UserDao userDao = db.userDao();
        fr = new fetch_recipes(activityReference, null, _RecipeModel);
        fr.execute();

        _RecipeDates = (ArrayList<DateModel>) userDao.getAll();
        try {
            setup_sample_RecipeDates();
            rv_today_position = get_rv_today_position();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String AppKey = "com.darkexceptionsoftware.thermomax_calendar";

        prefs = mContext.getSharedPreferences(
                AppKey, Context.MODE_PRIVATE);
        Boolean HAS_READ_EULA = prefs.getBoolean("HAS_READ_EULA", false);
        prefs = this.getSharedPreferences(
                AppKey, Context.MODE_PRIVATE);

        color1 = getResources().getColor(R.color.rv_back_1, mContext.getTheme());
        color2 = getResources().getColor(R.color.rv_back_2, mContext.getTheme());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.clickedFab1();
            }
        });
        binding.appBarMain.fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.clickedFab2();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Kochbuch, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rv_home) {
            mCallback.clickedHomebutton();
            return true;
        }

        if (id == R.id.action_rv_add){
            mCallback.clickedAddbutton();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setup_sample_RecipeDates() throws MalformedURLException, PackageManager.NameNotFoundException {
        for (int i = 0; i < 50; i++) {

            // _RecipeDates.add(new DateModel("RCP" + i + "X", RandomDateTime.randomtime(), "Fritz", 0, 0));
        }
        // _RecipeDates.add(new DateModel("RCP - now - X 2", new Date(), "Fritz", 0 ));

        DateFormat now = new SimpleDateFormat("dd-MM-yyyy");

        boolean foundTodayRecipes = false;
        for (DateModel temp : _RecipeDates) {
            if (now.format(temp.getDate()).equals(now.format(new Date())))
                foundTodayRecipes = true;

        }

        if (!foundTodayRecipes)
            _RecipeDates.add(new DateModel(new Long(0), new Date().getTime(), "Thermomax Info", -1, 0));


        _RecipeDates.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
    }

    public static class RandomDateTime {

        public static Date randomtime() {

            SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            int year = RandomDateTime.randBetween(2021, 2023);
            ;// Here you can set Range of years you need
            int month = RandomDateTime.randBetween(0, 11);
            int hour = RandomDateTime.randBetween(9, 22); //Hours will be displayed in between 9 to 22
            int min = RandomDateTime.randBetween(0, 59);
            int sec = RandomDateTime.randBetween(0, 59);


            GregorianCalendar gc = new GregorianCalendar(year, month, 1);
            int day = RandomDateTime.randBetween(1, gc.getActualMaximum(gc.DAY_OF_MONTH));

            gc.set(year, month, day, hour, min, sec);

            return gc.getTime();

        }


        public static int randBetween(int start, int end) {
            return start + (int) Math.round(Math.random() * (end - start));
        }
    }
}