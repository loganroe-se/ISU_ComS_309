package com.cs309.tutorial.tests;

public class Animal {

    private String name;

    private String species;

    private int age;

    private String attack;

    public Animal()
    {

    }

    public Animal(String name, String species, int age, String attack)
    {
        this.name = name;
        this.species = species;
        this.age = age;
        this.attack = attack;
    }

    public String getName()
    {
        return name;
    }

    public String getSpecies()
    {
        return species;
    }

    public int getAge()
    {
        return age;
    }

    public String getAttack()
    {
        return attack;
    }
}
