package org.example;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "letters")
public class Letter {

    @DatabaseField(id = true)
    private Integer id;

    @DatabaseField(columnName = "letter", defaultValue = "Unknown")
    private String letter;

    // Getter und Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

}