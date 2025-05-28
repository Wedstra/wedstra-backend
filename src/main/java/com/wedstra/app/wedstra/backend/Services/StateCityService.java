package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.StateCity;
import com.wedstra.app.wedstra.backend.Repo.StateCityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.*;

@Service
public class StateCityService {

    @Autowired
    private StateCityRepository repository;

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
