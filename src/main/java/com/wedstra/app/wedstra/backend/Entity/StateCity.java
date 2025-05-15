package com.wedstra.app.wedstra.backend.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Document(collection = "state_cities") // Replace with actual MongoDB collection name
public class StateCity {

    @Id
    private String id;

    private String state;

    private List<String> cities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }
}
