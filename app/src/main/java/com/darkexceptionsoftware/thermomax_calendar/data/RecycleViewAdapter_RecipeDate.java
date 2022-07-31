package com.darkexceptionsoftware.thermomax_calendar.data;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.darkexceptionsoftware.thermomax_calendar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class RecycleViewAdapter_RecipeDate extends RecyclerView.Adapter<RecycleViewAdapter_RecipeDate.MyViewHolder> {

    private static RecycleViewOnClickListener itemListener;


    Context context;
    ArrayList<DateModel> DateModels;
    RecyclerView.SmoothScroller smoothScroller;

    Date today = new Date();

    public RecyclerView.SmoothScroller getSmoothScroller() {
        return smoothScroller;
    }

    int rv_back_switch = 0;
    int backcolor;
    DateFormat time = new SimpleDateFormat("hh:mm");
    DateFormat day = new SimpleDateFormat("EE");
    DateFormat daydate = new SimpleDateFormat("dd");
    DateFormat now = new SimpleDateFormat("dd-MM-yyyy");
    DateFormat year = new SimpleDateFormat("yyyy");
    DateFormat month = new SimpleDateFormat("MMMM");

    public RecycleViewAdapter_RecipeDate(Context context, ArrayList<DateModel> DateModels, RecycleViewOnClickListener recyclerViewClickListener) {

        this.context = context;
        this.DateModels = DateModels;

        this.itemListener = recyclerViewClickListener;
        backcolor = context.getResources().getColor(R.color.rv_back_1, context.getTheme());

        smoothScroller = new
                LinearSmoothScroller(context) {
                    @Override protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
    }


    @NonNull
    @Override
    public RecycleViewAdapter_RecipeDate.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rcv_row_calendar, parent, false);
        return new RecycleViewAdapter_RecipeDate.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter_RecipeDate.MyViewHolder holder, int position) {

        String lastDay = "";
        String lastMonth = "";
        String lastyear = "";
        String lastnow = "";

        Date date = DateModels.get(position).getDate();

        String _year = year.format(date);
        String _month = month.format(date);
        String _now = now.format(date);

        holder.rv_year.setVisibility(View.INVISIBLE);
        holder.rv_cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cardview, context.getTheme()));
        holder.rv_imageview.setVisibility(View.INVISIBLE);
        holder.rv_fab.setVisibility(View.GONE);
        holder.rv_fab_add.setVisibility(View.GONE);
        holder.rv_day_name.setText(day.format(date));
        holder.rv_day_date.setText(daydate.format(date));
        holder.rv_month.setText(_month);
        holder.rv_year.setText(_year);
        holder.rv_separator.setVisibility(View.GONE);
        holder.rv_month.setVisibility(View.GONE);
        holder.rv_separator.setImageResource(R.color.black);
        holder.rv_back.setBackgroundColor(DateModels.get(position).getBackcolor());


        if (position > 0) {
            // get last item
            Date old_date = DateModels.get(position - 1).getDate();
            lastDay = daydate.format(old_date);
            lastMonth = month.format(old_date);
            lastyear = year.format(old_date);
            lastnow = now.format(old_date);
        }


        Boolean isToday = false;


        // HEUTE ???
        if (now.format(date).equals(now.format(today))) {
            isToday = true;
        }

        // IF TODAY, MARK THE DATE
        if (isToday) {
            holder.rv_cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_today, context.getTheme()));
            holder.rv_month.setText(holder.rv_month.getText().toString() + " - TODAY");
            holder.rv_separator.setVisibility(View.VISIBLE);
            holder.rv_month.setVisibility(View.VISIBLE);
            // NICHT DAS SELBE JAHR?
            if (!lastyear.equals(_year)) {
                holder.rv_year.setVisibility(View.VISIBLE);
            }
        }


        // NICHT DER SELBE MONAT?
        if (!lastMonth.equals(_month) || !lastyear.equals(_year) && lastMonth.equals(_month)) {
            holder.rv_separator.setVisibility(View.VISIBLE);
            holder.rv_month.setVisibility(View.VISIBLE);
            // NICHT DAS SELBE JAHR?
            if (!lastyear.equals(_year)) {
                holder.rv_year.setVisibility(View.VISIBLE);
            }

        }

        // DERSELBE TAG?
        if (!now.equals(lastnow)) {
            holder.rv_day_date.setVisibility(View.VISIBLE);
            holder.rv_day_name.setVisibility(View.VISIBLE);

        }


        if (DateModels.get(position).getStatus() == -1) {

            // KEIN WIRKLICHER EINTRAG, SONDERN HEUTE
            holder.rv_fab_add.setVisibility(View.VISIBLE);
            holder.rv_recipe.setText(DateModels.get(position).getModel());
            holder.rv_time.setText("Wähle ein Rezeot mit dem Add-Button aus!");

        } else {
            holder.rv_imageview.setVisibility(View.VISIBLE);
            holder.rv_imageview.setImageResource(R.drawable.ic_baseline_dining_24);

            holder.rv_fab.setVisibility(View.VISIBLE);

            holder.rv_recipe.setText(DateModels.get(position).getModel() + " " + now.format(date));
            holder.rv_time.setText(time.format(date));
        }


    }

    @Override
    public int getItemCount() {
        return DateModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView rv_recipe, rv_time, rv_day_name, rv_day_date, rv_month, rv_year;
        CardView rv_cardView;
        FloatingActionButton rv_fab, rv_fab_add;
        ConstraintLayout rv_back;


        ImageView rv_imageview, rv_separator;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_back = itemView.findViewById(R.id.rv_back);
            rv_cardView = itemView.findViewById(R.id.rv_cardView);
            rv_imageview = itemView.findViewById(R.id.rv_imageview);
            rv_separator = itemView.findViewById(R.id.rv_separator);
            rv_recipe = itemView.findViewById(R.id.rv_recipe);
            rv_time = itemView.findViewById(R.id.rv_time);
            rv_day_name = itemView.findViewById(R.id.rv_day_name);
            rv_day_date = itemView.findViewById(R.id.rv_day_date);

            rv_month = itemView.findViewById(R.id.rv_month);
            rv_year = itemView.findViewById(R.id.rv_year);

            rv_fab = itemView.findViewById(R.id.rv_fab);
            rv_fab_add = itemView.findViewById(R.id.rv_fab_add);

            rv_fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onItemClick(getAdapterPosition(), "delete");
                }
            });

            rv_fab_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onItemClick(getAdapterPosition(), "add");
                }
            });
        }


    }



}