package azynias.study.ObjectModels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Albedo on 6/20/2017.
 */

public class Kanji implements Serializable {
    private String elements;
    private String meaning;
    private int ID;
    private String character;
    private String story;
    private int bracketId;
    private Date dueDate;

    private String exampleStory;


    public Kanji() {

    }

    public Kanji(String elements, String name, int ID, String exampleStory, int bracket) {
        this.elements = elements;
        this.meaning = name;
        this.ID = ID;
        this.exampleStory = exampleStory;
        this.bracketId = bracket;
    }

    public String getStory() {
        return this.exampleStory;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getCharacter() {return this.character;}

    public String getElements() {
        return elements;
    }

    public void setElements(String elements) {
        this.elements = elements;
    }

    public String getName() {
        return meaning;
    }

    public void setName(String name) {
        this.meaning = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getExampleStory() {
        return exampleStory;
    }

    public void setExampleStory(String exampleStory) {
        this.exampleStory = exampleStory;
    }

}
