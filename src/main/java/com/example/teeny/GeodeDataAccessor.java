package com.example.teeny;

import java.util.Map.Entry;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


@Component
@Primary
public class GeodeDataAccessor implements DataAccessor {

  ClientCache cache;
  Region<String, String> teenyRegion;
  Region<String, Integer> statsRegion;

  public GeodeDataAccessor() {
    cache = new ClientCacheFactory().addPoolLocator("localhost", 10334).create();

  }

  @Override
  public void addTeeny(Teeny teeny) {

  }

  @Override
  public Teeny accessTeeny(String id) {
    return null;
  }

  @Override
  public int size() {
    return teenyRegion.keySetOnServer().size();
  }

  @Override
  public Teeny[] getAll() {
    Teeny[] result = new Teeny[size()];

    return result;
  }

  @Override
  public void clear() {
    teenyRegion.clear();
    statsRegion.clear();
  }

  @Override
  public Teeny removeTeeny(String id) {
    return null;
  }
}
