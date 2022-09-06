package com.darkexceptionsoftware.thermomax_calendar.ui.ingredient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.IndrigentParser;
import com.darkexceptionsoftware.thermomax_calendar.data.if_RecycleViewOnClickListener;

import java.util.ArrayList;
import java.util.Locale;

public class RecycleViewAdapter_Ingredient extends RecyclerView.Adapter<RecycleViewAdapter_Ingredient.MyViewHolder> {

    private static if_RecycleViewOnClickListener itemListener;

    int lastposition = 0;

    Context context;
    ArrayList<Indrigent> Einkaufsliste;
    RecyclerView.SmoothScroller smoothScroller;


    Activity activityReference;

    public RecycleViewAdapter_Ingredient(Activity activityReference, ArrayList<Indrigent> Einkaufsliste, if_RecycleViewOnClickListener recyclerViewClickListener) {

        this.activityReference = activityReference;
        this.context = activityReference.getApplicationContext();
        this.Einkaufsliste = Einkaufsliste;

        this.itemListener = recyclerViewClickListener;

        smoothScroller = new
                LinearSmoothScroller(context) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
    }

    public RecyclerView.SmoothScroller getSmoothScroller() {
        return smoothScroller;
    }

    @NonNull
    @Override
    public RecycleViewAdapter_Ingredient.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rcv_row_ingredient, parent, false);
        return new RecycleViewAdapter_Ingredient.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter_Ingredient.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final Indrigent z = Einkaufsliste.get(position);

        holder.rv_ingredient.setText(z.getName());
        holder.rv_indicator.setBackgroundResource(R.color.ind_none);

        if (z.getSortof() == IndrigentParser.BEILAGE)
            holder.rv_indicator.setBackgroundResource(R.color.ind_beilage);

        if (z.getSortof() == IndrigentParser.GEMÜSE)
            holder.rv_indicator.setBackgroundResource(R.color.ind_gemüse);

        if (z.getSortof() == IndrigentParser.FLEISCH)
            holder.rv_indicator.setBackgroundResource(R.color.ind_fleisch);

        if (z.getSortof() == IndrigentParser.MOPRO)
            holder.rv_indicator.setBackgroundResource(R.color.ind_mopro);

        if (z.getSortof() == IndrigentParser.FLÜSSIGES)
            holder.rv_indicator.setBackgroundResource(R.color.ind_flüssiges);

        if (z.getSortof() == IndrigentParser.GEWÜRZ) {
            holder.rv_indicator.setBackgroundResource(R.color.ind_gewürz);
            holder.rv_amount.setVisibility(View.GONE);
            holder.rv_amountof.setVisibility(View.GONE);
        }else{

        }

        String Amount = z.getAmount().toString();
        String AmountOf = z.getAmountof();
        if (Amount.substring(Amount.length() -2).equals(".0"))
            Amount = Amount.substring(0, Amount.length() -2);

        holder.rv_amount.setVisibility(View.VISIBLE);
        holder.rv_amountof.setVisibility(View.VISIBLE);
        holder.rv_amount.setText(Amount);
        holder.rv_amountof.setText(AmountOf);

        AmountOf = AmountOf.toLowerCase(Locale.ROOT);

        if (Amount.equals("0") || AmountOf.matches("el|tl|etwas")  ){
            holder.rv_amount.setVisibility(View.GONE);
            holder.rv_amountof.setVisibility(View.GONE);
        }

        lastposition = position;
    }

    @Override
    public int getItemCount() {
        return Einkaufsliste.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView rv_cardview;
        TextView rv_amount, rv_amountof, rv_ingredient;
        Button rcv_ind_btn1, rcv_ind_btn2, rcv_ind_btn3;
        View rv_indicator;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_cardview = itemView.findViewById(R.id.rv_cardView);
            rv_amount = itemView.findViewById(R.id.rv_amount);
            rv_amountof = itemView.findViewById(R.id.rv_amountof);
            rv_ingredient = itemView.findViewById(R.id.rv_ingredient);


            rv_indicator = itemView.findViewById(R.id.rv_indicator);


            rv_cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onItemClick(getAdapterPosition(),"show");
                }
            });

            rv_cardview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemListener.onItemlongClick(getAdapterPosition(),"show");
                    return false;
                }
            });
        }


        @Override
        public void onClick(View view) {
            itemListener.onViewClick(view, "clicked");
        }
    }
}
