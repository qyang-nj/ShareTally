package me.qingy.sharetally.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.DatabaseTable;
import com.parse.ParseException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.qingy.sharetally.Log.Logger;
import me.qingy.sharetally.model.*;

/**
 * Created by qing on 10/22/14.
 */

@DatabaseTable
public class Tally {
    public static final String KEY_ID = "tally_id";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String objId;

    @DatabaseField
    private Date updateAt;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    //@ForeignCollectionField(eager = true)
    ForeignCollection<Person> participants;

    //@ForeignCollectionField(eager = false)
    ForeignCollection<Record> records;

    public Tally() {
        /* for ORMLite */
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addRecord(Record r) {
        records.add(r);
    }

    public void delRecord(Record r) {
        records.remove(r);
    }

    public List<Record> getRecords() {
        return new ArrayList<Record>() {
            {
                addAll(records);
            }
        };
    }

    public boolean hasRecord() {
        return records != null && records.size() > 0;
    }

    private PreparedQuery<Person> participantQuery;

    public List<Person> getParticipants(RuntimeExceptionDao<Person, Integer> personDao, RuntimeExceptionDao<TallyParticipant, Long> tallyParticipantDao) {
        try {
            if (participantQuery == null) {
                QueryBuilder<TallyParticipant, Long> participantQb = tallyParticipantDao.queryBuilder();
                /* select the person id */
                participantQb.selectColumns(TallyParticipant.FIELD_PERSON_ID);
                SelectArg userSelectArg = new SelectArg();
                participantQb.where().eq(TallyParticipant.FIELD_TALLY_ID, userSelectArg);

                QueryBuilder<Person, Integer> personQb = personDao.queryBuilder();
                personQb.where().in(Person.FIELD_ID, participantQb);
                participantQuery = personQb.prepare();
            }
            participantQuery.setArgumentHolderValue(0, this);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return personDao.query(participantQuery);
    }

    public void setParticipants(List<Person> participants, RuntimeExceptionDao<TallyParticipant, Long> tallyParticipantDao) {
        try {
            DeleteBuilder<TallyParticipant, Long> delQb = tallyParticipantDao.deleteBuilder();
            delQb.where().eq(TallyParticipant.FIELD_TALLY_ID, getId());
            PreparedDelete<TallyParticipant> participantDel = delQb.prepare();
            tallyParticipantDao.delete(participantDel);
        } catch (SQLException e) {
            return;
        }

        for (Person p : participants) {
            tallyParticipantDao.create(new TallyParticipant(this, p));
        }
    }

    public Map<Person, Result> calculate() {
//        Map<me.qingy.sharetally.model.Person, Result> result = new HashMap<me.qingy.sharetally.model.Person, Result>();
//        List<me.qingy.sharetally.model.Record> records = getRecords();
//        List<me.qingy.sharetally.model.Person> participants = getParticipants();
//
//        for (me.qingy.sharetally.model.Person p : participants) {
//            result.put(p, new Result());
//        }
//
//        for (me.qingy.sharetally.model.Record r : records) {
//            List<Double> weights = r.getBeneficiaryWeights();
//            me.qingy.sharetally.model.Person payer = r.getPayer();
//            double totalWeight = 0.0;
//            for (int i = 0; i < participants.size(); ++i) {
//                if (i < weights.size()) {
//                    totalWeight += weights.get(i);
//                }
//            }
//
//            double paid = r.getAmount();
//            result.get(payer).paid += paid;
//
//            for (int i = 0; i < participants.size(); ++i) {
//                if (i < weights.size()) {
//                    result.get(participants.get(i)).toPay += paid * weights.get(i) / totalWeight;
//                }
//            }
//        }
//
//        return result;
        return null;
    }

    public class Result {
        public double paid = 0;
        public double toPay = 0;
    }
}