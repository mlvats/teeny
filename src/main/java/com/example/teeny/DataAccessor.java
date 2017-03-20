package com.example.teeny;

import java.util.List;

public interface DataAccessor {
  public void addTeeny(Teeny teeny);
  public Teeny accessTeeny(String id);
  public Teeny removeTeeny(String id);
  public int size();
  public List<Teeny> getAll();
  public void clear();
}
