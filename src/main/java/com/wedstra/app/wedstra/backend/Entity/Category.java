package com.wedstra.app.wedstra.backend.Entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "categories")
public class Category {
    private String id;
    private String category_name;
    private String color;
    private List<String> sub_category;

    public Category(String category_name) {
        this.category_name = category_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public List<String> getSub_category() {
        return sub_category;
    }

    public void setSub_category(List<String> sub_category) {
        this.sub_category = sub_category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
