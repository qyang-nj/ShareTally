package me.qingy.tallyfriend.model;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.qingy.tallyfriend.Log.Logger;

/**
 * Created by YangQ on 9/17/2014.
 */
@ParseClassName("Record")
public class Record extends ParseObject {
    private static final String KEY_CAPTION = "caption";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DATE = "date";
    private static final String KEY_PAYER = "payer";
    private static final String KEY_ALL_EQUAL = "allEqual";
    private static final String KEY_WEIGHT = "weights";

    public static void fetchRecordInBackground(String id, GetCallback<Record> cb) {
        ParseQuery<Record> query = ParseQuery.getQuery(Record.class);
        query.fromLocalDatastore();
        query.getInBackground(id, cb);
    }

    public String getLabel() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public double getAmount() {
        return getDouble(KEY_AMOUNT);
    }

    public void setAmount(double amount) {
        put(KEY_AMOUNT, amount);
    }

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public Person getPayer() {
        Person p = null;
        try {
            ParseObject po = getParseObject(KEY_PAYER);
            if (po != null) {
                p = po.fetchIfNeeded();
            }
        } catch (ParseException e) {
            Logger.e(e.getMessage());
            e.printStackTrace();
        }
        return p;
    }

    public void setPayer(Person payer) {
        put(KEY_PAYER, payer);
    }

    public boolean isAllEqual() {
        return getBoolean(KEY_ALL_EQUAL);
    }

    public void setAllEqual(boolean allEqual) {
        put(KEY_ALL_EQUAL, allEqual);
    }

    public List<Double> getBeneficiaryWeights() {
        List<Double> weights = null;
        List<Number> numbers = getList(KEY_WEIGHT);
        if (numbers != null) {
            weights = new ArrayList<Double>();
            for (Number n : numbers) {
                weights.add(n.doubleValue());
            }
        }
        return weights;
    }

    /* There is a assumption that the oder of weights is the same as the order of participants in Tally. */
    public void setBeneficiaryWeights(List<Double> beneficiaryWeights) {
        remove(KEY_WEIGHT);
        addAll(KEY_WEIGHT, beneficiaryWeights);
    }
    //private static final String KEY_PICTURE = "picture";
}
