package com.wedstra.app.wedstra.backend.Services;

import com.mongodb.client.MongoCollection;
import com.wedstra.app.wedstra.backend.Entity.StateCity;
import com.wedstra.app.wedstra.backend.Entity.Vendor;
import com.wedstra.app.wedstra.backend.Repo.StateCityRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.*;

@Service
public class StateCityService {

    @Autowired
    private StateCityRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> getAllStates() {
        List<String> states = new ArrayList<>();
        List<StateCity> stateCities = repository.findAll();

        for (StateCity location : stateCities) {
            if (location.getState() != null) {
                states.add(location.getState());
            }
        }
        Collections.sort(states);
        return states;
    }

    public List<String> getDistinctStatesFromVendors() {
        List<String> states = mongoTemplate.query(Vendor.class)
                .distinct("state")
                .as(String.class)
                .all();
        Collections.sort(states);
        return states;
    }

    public List<String> getDistinctCitiesFromVendors(String state) {
        // 1. Get the name of the collection from your Vendor class
        // This reads the @Document("vendor") annotation
        String collectionName = mongoTemplate.getCollectionName(Vendor.class);

        // 2. Get the raw MongoDB collection object
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        // 3. Create the filter for the state
        Query query = new Query(Criteria.where("state").is(state));

        // 4. Execute the distinct command directly on the collection
        // and collect the results into an ArrayList
        List<String> cities = collection
                .distinct("city", query.getQueryObject(), String.class)
                .into(new ArrayList<>());

        // 5. Sort the results
        Collections.sort(cities);
        return cities;
    }


    public List<String> getCitiesByState(String state) {
        List<String> cities = new ArrayList<>();
        List<StateCity> stateCities = repository.findAll();

        for (StateCity location : stateCities) {
            if (location.getState() != null && location.getState().equalsIgnoreCase(state)) {
                if (location.getCities() != null) {
                    cities.addAll(location.getCities());
                }
            }
        }

        Collections.sort(cities);
        return cities;
    }


//    public List<String> getCitiesByState(String stateName) {
//        List<StateCity> allData = repository.findAll();
//        for (StateCity data : allData) {
//            if (data.getStateCities() != null && data.getStateCities().containsKey(stateName)) {
//                return data.getStateCities().get(stateName);
//            }
//        }
//        return Collections.emptyList();
//    }
}
