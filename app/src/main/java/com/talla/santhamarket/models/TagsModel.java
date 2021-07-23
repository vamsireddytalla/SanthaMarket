package com.talla.santhamarket.models;

import java.util.List;

public class TagsModel
{
    private List<String> tagsList;

    public List<String> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<String> tagsList) {
        this.tagsList = tagsList;
    }

    @Override
    public String toString() {
        return "TagsModel{" +
                ", tagsList=" + tagsList +
                '}';
    }
}
