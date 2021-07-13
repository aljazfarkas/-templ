package com.pora.lib;

import java.util.ArrayList;

public class People {
    private ArrayList<Person> people;

    public People(){
        this.people = new ArrayList<Person>();
    }
    public People(ArrayList<Person> people) {
        this.people = people;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }

}