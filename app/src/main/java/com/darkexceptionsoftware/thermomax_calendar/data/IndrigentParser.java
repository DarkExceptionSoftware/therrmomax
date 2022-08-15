package com.darkexceptionsoftware.thermomax_calendar.data;

import android.app.Activity;

import com.darkexceptionsoftware.thermomax_calendar.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class IndrigentParser {

    public static final int NONE = 99;
    public static final int GEWÜRZ = 1;
    public static final int MOPRO = 2;
    public static final int FLEISCH = 3;
    public static final int GEMÜSE = 4;
    public static final int BEILAGE = 5;
    public static final int FLÜSSIGES = 6;
    public static HashMap<String, Integer> sortof_HashMap;
    public static Activity activityReference;
    public IndrigentParser(Activity activityReference) {
        this.activityReference = activityReference;

        sortof_HashMap = new HashMap<String, Integer>();
        sortof_HashMap.putIfAbsent("", NONE);

        List<String> AddCat;

        String tag = loadTags();
        tag = tag.replace("\r","");
        tag = tag.replace("\n","");
        String Tags[] = tag.split("#");

        for (int i = 0; i < Tags.length; i++) {
            String splitTags[] = Tags[i].split(",");
            for (String item : splitTags){
                sortof_HashMap.put(item.trim(), i + 1);
            }
        }
    }

    public static List<Indrigent> outputList(List<Indrigent> inputList) {

        List<Indrigent> resultList = new ArrayList<>();

        for (Indrigent i : inputList) {
            String iName = i.getName();

            iName = iName.replace("(n)", "n");
            iName = iName.replace("(er)", "er");
            iName = iName.replace(",", " ");

            String splitNames[] = iName.trim().split(" ");

            String indrigentToAdd = splitNames[0];
            Float amount = i.getAmount();
            String amountOf = i.getAmountof();

            int sortOf = sortof_HashMap.getOrDefault(indrigentToAdd, NONE);
            if (sortOf == NONE)
                sortOf = sortof_HashMap.getOrDefault(indrigentToAdd.substring(0, indrigentToAdd.length() - 1), NONE);


            boolean rFound = false;
            for (Indrigent rSearch : resultList) {
                if (rSearch.getName().equals(indrigentToAdd)) {

                    Float rAmount = rSearch.getAmount();
                    amount = rAmount + amount;

                    if (sortOf == GEWÜRZ) {
                        amount = 0f;
                        amountOf = "";
                    }

                    rFound = true;
                    break;
                }
            }

            if (!rFound) {
                if (sortOf == GEWÜRZ) {
                    resultList.add(new Indrigent(0f, "", indrigentToAdd, sortOf));
                } else {
                    resultList.add(new Indrigent(amount, amountOf, indrigentToAdd, sortOf));
                }
            }

            if (splitNames.length > 1) {
                if (splitNames[1].equals("und")) {
                    indrigentToAdd = splitNames[2];
                    sortOf = sortof_HashMap.getOrDefault(indrigentToAdd, NONE);
                    if (sortOf == NONE)
                        sortOf = sortof_HashMap.getOrDefault(indrigentToAdd + "n", NONE);

                    rFound = false;
                    for (Indrigent rSearch : resultList) {
                        if (rSearch.getName().equals(indrigentToAdd)) {
                            // Float rAmount = rSearch.getAmount();
                            // rSearch.setAmount(rAmount + amount);
                            rFound = true;
                            break;
                        }
                    }

                    if (!rFound) {
                        if (sortOf == GEWÜRZ) {
                            resultList.add(new Indrigent(0f, "", indrigentToAdd, sortOf));
                        } else {
                            resultList.add(new Indrigent(amount, amountOf, indrigentToAdd, sortOf));
                        }
                    }
                }
            }
        }
        resultList.sort(Comparator.comparing(Indrigent::getSortof));

        return resultList;
    }

    public static String loadTags() {
        InputStream inputStream = activityReference.getResources().openRawResource(R.raw.tags);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (
                IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
