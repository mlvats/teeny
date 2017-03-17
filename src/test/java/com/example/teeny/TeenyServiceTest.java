package com.example.teeny;

import com.example.teeny.TeenyService.Meta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TeenyService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TeenyServiceTest {

  String url1 = "www.yahoo.com";
  String url1Hash = Integer.toString(url1.hashCode(), 36);
  String url2 = "www.google.com";
  String url2Hash = Integer.toString(url2.hashCode(), 36).substring(1);

  @LocalServerPort
  int port;

  String postUrl;
  String getUrl;

  TestRestTemplate rest = new TestRestTemplate();

  @Before
  public void setup() {
    postUrl = "http://localhost:" + port;
    getUrl = "http://localhost:" + port + "/{id}";
    rest.delete(postUrl);
  }

  @Test
  public void shouldCreateOneTeeny() {
    MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
    vars.add("url", url1);

    postUrlAndVerifyHash(vars, url1Hash);
    verifyUrl(url1Hash, url1);
  }

  @Test
  public void shouldReturnOneTeeny() {
    MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();

    vars.add("url", url1);
    postUrlAndVerifyHash(vars, url1Hash);
    verifyUrl(url1Hash, url1);
    
    ResponseEntity<Teeny[]> response = rest.getForEntity(postUrl, Teeny[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().length);
    assertEquals(1, response.getBody()[0].getPopularity());
  }

  @Test
  public void shouldReturnValidCount() {
    MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
    vars.add("url", url1);
    postUrlAndVerifyHash(vars, url1Hash);
    verifyCount(rest, postUrl, 1);

    vars.clear();
    vars.add("url", url2);
    postUrlAndVerifyHash(vars, url2Hash);
    verifyCount(rest, postUrl, 2);
  }

  @Test
  public void shouldListAllTeeny() {
    MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
    vars.add("url", url1);
    postUrlAndVerifyHash(vars, url1Hash);
    ResponseEntity<Teeny[]> response = rest.getForEntity(postUrl, Teeny[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().length);
    assertEquals(url1, response.getBody()[0].getUrl());
    
    vars.clear();
    vars.add("url", url2);
    postUrlAndVerifyHash(vars, url2Hash);
    response = rest.getForEntity(postUrl, Teeny[].class, "false");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().length);
  }
  
  @Test
  public void shouldDeleteTeeny() {
    MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
    vars.add("url", url1);
    postUrlAndVerifyHash(vars, url1Hash);
    verifyCount(rest, postUrl, 1);

    vars.clear();
    vars.add("url", url2);
    postUrlAndVerifyHash(vars, url2Hash);
    verifyCount(rest, postUrl, 2);

    rest.delete(getUrl, url1Hash);
    verifyCount(rest, postUrl, 1);

    rest.delete(getUrl, url2Hash);
    verifyCount(rest, postUrl, 0);
  }

  @Test
  public void shouldAdd2TeenyAndReturnValidTeeny() {
    MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
    vars.add("url", url1);
    postUrlAndVerifyHash(vars, url1Hash);

    vars.clear();
    vars.add("url", url2);
    postUrlAndVerifyHash(vars, url2Hash);

    verifyUrl(url1Hash, url1);
    verifyUrl(url2Hash, url2);

    assertNotEquals(url2, url1);
  }

  @Test
  public void shouldAllowDuplicateEntry() {
    MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
    vars.add("url", url1);

    postUrlAndVerifyHash(vars, url1Hash);
    verifyCount(rest, postUrl, 1);

    postUrlAndVerifyHash(vars, url1Hash);
    verifyCount(rest, postUrl, 1);
  }

  @Test
  public void emptyMapShouldNotFail() {
    String getUrl = "http://localhost:" + port;

    TestRestTemplate rest = new TestRestTemplate();
    ResponseEntity<String> response = rest.getForEntity(getUrl, String.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("[]", response.getBody());
  }

  private void postUrlAndVerifyHash(MultiValueMap<String, String> vars, String hash) {
    ResponseEntity<String> response = rest.postForEntity(postUrl, vars, String.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(hash, response.getBody());
  }

  private void verifyUrl(String hash, String url) {
    ResponseEntity<String> response = rest.getForEntity(getUrl, String.class, hash);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(url, response.getBody());
  }

  private void verifyCount(TestRestTemplate rest, String postUrl, int count) {
    ResponseEntity<Meta> response;
    response = rest.getForEntity(postUrl + "/?verbose={verbose}", Meta.class, "false");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(count, response.getBody().getCount());
  }
}