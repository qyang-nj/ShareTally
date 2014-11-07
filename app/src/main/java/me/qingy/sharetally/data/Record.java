package me.qingy.sharetally.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by qing on 10/22/14.
 */

@DatabaseTable
public class Record {
    public static final String KEY_ID = "record_id";
    public static final int ID_NEW = 0;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String objId;

    @DatabaseField
    private Date updateAt;

    @DatabaseField
    private double amount;

    @DatabaseField
    private String label;

    @DatabaseField
    private Date date;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Person payer;

    @ForeignCollectionField(eager = true)
    ForeignCollection<ParticipantWeight> weights;

    @DatabaseField(foreign = true)
    private Tally tallyId;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] receiptImage;

    public Record() {
        /* for ORMLite */
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setCaption(String label) {
        this.label = label;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Person getPayer() {
        return this.payer;
    }

    public void setPayer(Person payer) {
        this.payer = payer;
    }

    public void setReceiptImage(Bitmap bmp) {
        if (bmp == null) {
            receiptImage = null;
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        receiptImage = stream.toByteArray();
    }

    public Bitmap getReceiptImage() {
        if (receiptImage == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(receiptImage, 0, receiptImage.length);
    }

    public HashMap<Person, Double> getBeneficiaryWeights() {
        if (weights == null || weights.size() == 0) {
            return null;
        }

        return new HashMap<Person, Double>() {
            {
                for (ParticipantWeight pw : weights) {
                    put(pw.getParticipant(), pw.getWeight());
                }
            }
        };
    }

    public void setBeneficiaryWeights(List<Person> beneficiaries, List<Double> weights) {
        this.weights.clear();
        for (int i = 0; i < beneficiaries.size(); ++i) {
            this.weights.add(new ParticipantWeight(this, beneficiaries.get(i), weights.get(i)));
        }
    }
}
