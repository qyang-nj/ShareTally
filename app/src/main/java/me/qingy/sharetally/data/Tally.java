package me.qingy.sharetally.data;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qing on 10/22/14.
 */

@DatabaseTable
public class Tally {
    public static final String KEY_ID = "tally_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String objId;

    @DatabaseField
    private Date updateAt;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    @ForeignCollectionField(eager = true)
    ForeignCollection<Record> records;

    public Tally() {
        /* for ORMLite */
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tally) {
            return ((Tally) o).getId() == id;
        }
        return false;
    }

    public int getId() {
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

    public List<Person> getParticipants(RuntimeExceptionDao<Person, Integer> personDao, RuntimeExceptionDao<TallyParticipant, Integer> tallyParticipantDao) {
        try {
            if (participantQuery == null) {
                QueryBuilder<TallyParticipant, Integer> participantQb = tallyParticipantDao.queryBuilder();
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

    public void setParticipants(List<Person> participants, RuntimeExceptionDao<TallyParticipant, Integer> tallyParticipantDao) {
        try {
            DeleteBuilder<TallyParticipant, Integer> delQb = tallyParticipantDao.deleteBuilder();
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

    public Map<Person, Result> calculate(RuntimeExceptionDao<Person, Integer> personDao, RuntimeExceptionDao<TallyParticipant, Integer> tallyParticipantDao) {
        Map<Person, Result> result = new HashMap<Person, Result>();
        List<Record> records = getRecords();
        List<Person> participants = getParticipants(personDao, tallyParticipantDao);

        for (Person p : participants) {
            result.put(p, new Result());
        }

        for (Record r : records) {
            Map<Person, Double> weights = r.getBeneficiaryWeights();
            Person payer = r.getPayer();
            double totalWeight = 0.0;
            for (Person p : participants) {
                totalWeight += weights.containsKey(p) ? weights.get(p) : 0.0;
            }

            double paid = r.getAmount();
            result.get(payer).paid += paid;

            for (Person p : participants) {
                result.get(p).toPay += paid * (weights.containsKey(p) ? weights.get(p) : 0.0) / totalWeight;
            }
        }

        return result;
    }

    public class Result {
        public double paid = 0;
        public double toPay = 0;
    }
}
