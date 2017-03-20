package com.example.teeny;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class LocalMapDataAccessor implements DataAccessor {
  ConcurrentHashMap<String, Teeny> map = new ConcurrentHashMap<>();

  @Override
  public void addTeeny(Teeny teeny) {
    map.put(teeny.getId(), teeny);
  }

  @Override
  public Teeny accessTeeny(String id) {
    map.get(id).incrementPopularity();
    return map.get(id);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public List<Teeny> getAll() {
    List<Teeny> result = new ArrayList<>(map.size());
    for (Entry<String, Teeny> entry : map.entrySet()) {
      result.add(entry.getValue());
    }
    return result;
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Teeny removeTeeny(String id) {
    return map.remove(id);
  }
}
