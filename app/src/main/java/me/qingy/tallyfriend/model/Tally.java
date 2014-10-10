package me.qingy.tallyfriend.model;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.qingy.tallyfriend.Log.Logger;

/**
 * Created by YangQ on 9/17/2014.
 */
@ParseClassName("Tally")
public class Tally extends ParseObject {
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PARTICIPANTS = "participants";
    private static final String KEY_RECORDS = "records";

    public static void fetchTallyInBackground(String id, GetCallback<Tally> cb) {
        ParseQuery<Tally> query = ParseQuery.getQuery(Tally.class);
        query.fromLocalDatastore();
        query.getInBackground(id, cb);
    }

    public static void fetchTallyListInBackground(FindCallback<Tally> cb) {
        ParseQuery<Tally> query = ParseQuery.getQuery(Tally.class);
        query.fromLocalDatastore();
        query.findInBackground(cb);
    }

    public void pin() {
        try {
            super.pin();
            super.saveEventually();
        } catch (ParseException e) {
            Logger.e(e.getMessage());
        }
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void addRecord(Record r) {
        addAllUnique(KEY_RECORDS, Arrays.asList(r));
    }

    public void delRecord(Record r) {
        removeAll(KEY_RECORDS, Arrays.asList(r));
    }

    public List<Record> getRecords() {
        List<Record> records = getList(KEY_RECORDS);
        if (records != null) {
            for (Record r : records) {
                try {
                    r.fetchIfNeeded();
                } catch (ParseException e) {
                    Logger.e(e.getMessage());
                    e.printStackTrace();
                }
            }

            Collections.sort(records, new Comparator<Record>() {
                @Override
                public int compare(Record lhs, Record rhs) {
                    return -lhs.getDate().compareTo(rhs.getDate());
                }
            });
        }

        return records;
    }

    public boolean hasRecord() {
        List<Record> records = getList(KEY_RECORDS);
        return records != null && records.size() > 0;
    }

    public List<Person> getParticipants() {
        List<Person> participants = getList(KEY_PARTICIPANTS);
        if (participants == null) {
            return null;
        }

        for (Person p : participants) {
            try {
                p.fetchFromLocalDatastore();
            } catch (ParseException e) {
                Logger.e(e.getMessage());
                e.printStackTrace();
            }
        }
        return participants;
    }

    public void setParticipants(List<Person> participants) {
        remove(KEY_PARTICIPANTS);
        addAll(KEY_PARTICIPANTS, participants);
    }

    public Map<Person, Result> calculate() {
        Map<Person, Result> result = new HashMap<Person, Result>();
        List<Record> records = getRecords();
        List<Person> participants = getParticipants();

        for (Person p : participants) {
            result.put(p, new Result());
        }

        for (Record r : records) {
            List<Double> weights = r.getBeneficiaryWeights();
            Person payer = r.getPayer();
            double totalWeight = 0.0;
            for (int i = 0; i < participants.size(); ++i) {
                if (i < weights.size()) {
                    totalWeight += weights.get(i);
                }
            }

            double paid = r.getAmount();
            result.get(payer).paid += paid;

            for (int i = 0; i < participants.size(); ++i) {
                if (i < weights.size()) {
                    result.get(participants.get(i)).toPay += paid * weights.get(i) / totalWeight;
                }
            }
        }

        return result;
    }

    public class Result {
        public double paid = 0;
        public double toPay = 0;
    }
}
