package me.qingy.tallyfriend;

import android.content.Context;

import java.util.List;

import me.qingy.tallyfriend.model.Person;

/**
 * Created by YangQ on 9/26/2014.
 */
public class PersonNameAdapter extends PersonAdapter {
    public PersonNameAdapter(Context context, List<Person> people) {
        super(context, people);
        setLayout(android.R.layout.simple_list_item_1);
    }
}
