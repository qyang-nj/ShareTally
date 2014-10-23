package me.qingy.sharetally.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by qing on 10/22/14.
 */

@DatabaseTable
public class Person {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String objId;

    @DatabaseField
    private Date updateAt;

    @DatabaseField
    private String name;

    @DatabaseField
    private String email;

    public Person() {
        /* for ORMLite */
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String  getEmail() {
        return email;
    }
}
