package com.qcadoo.mes.beans.dictionaries;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dictionaries_dictionary_item")
public final class DictionariesDictionaryItem {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    private boolean deleted;

    @ManyToOne
    private DictionariesDictionary dictionary;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public DictionariesDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(final DictionariesDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

}
