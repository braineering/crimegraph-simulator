/*
  The MIT License (MIT)

  Copyright (c) 2016 Giacomo Marciani and Michele Porretta

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:


  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.


  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
 */

package com.acmutv.crimegraph_monitor.core.db;

import com.acmutv.crimegraph_monitor.core.link.Link;
import com.acmutv.crimegraph_monitor.core.link.LinkType;
import org.junit.*;
import org.neo4j.driver.v1.*;

import java.util.*;

import static com.acmutv.crimegraph_monitor.Common.*;
import static org.neo4j.driver.v1.Values.parameters;

/**
 * JUnit test suite for {@link Neo4JManager}.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see Neo4JManager
 */
public class Neo4JManagerEvaluationTest {

  private static Driver DRIVER;

  private static final String MATCH =
      "MATCH (a:Person {id:{src}})-[r:%s {weight:{weight}}]->(b:Person {id:{dst}}) " +
          "RETURN a.id as src, b.id as dst, r.weight as weight";

  private static final List<Link> DATA = new LinkedList<Link>(){{
    add(new Link(1,2,10.0, LinkType.REAL));
    add(new Link(1,3,10.0, LinkType.REAL));
    add(new Link(2,3,10.0, LinkType.REAL));
    add(new Link(2,4,10.0, LinkType.REAL));
    add(new Link(3,6,10.0, LinkType.REAL));
    add(new Link(4,5,10.0, LinkType.REAL));
    add(new Link(6,7,10.0, LinkType.REAL));
  }};

  @BeforeClass
  public static void init() {
    DbConfiguration dbconf = new DbConfiguration(HOSTNAME, USERNAME, PASSWORD);
    boolean neo4jActive = true;
    try {
      Neo4JManager.empyting(dbconf);
    } catch (Exception exc) {
      neo4jActive = false;
    }
    Assume.assumeTrue(neo4jActive);
    DRIVER = Neo4JManager.open(dbconf);

    Session session = DRIVER.session();

    for (Link link : DATA) Neo4JManager.save(session, link);

    // Check
    for (Link link : DATA) {
      String query = String.format(MATCH, link.getType().name());
      Value params = parameters("src", link.getSrc(), "dst", link.getDst(), "weight", link.getWeight());
      StatementResult result = session.run(query, params);
      Assert.assertTrue(result.hasNext());
      Record record = result.next();
      Long src = record.get("src").asLong();
      Long dst = record.get("dst").asLong();
      Double weight = record.get("weight").asDouble();
      Assume.assumeTrue(link.getSrc().equals(src));
      Assume.assumeTrue(link.getDst().equals(dst));
      Assume.assumeTrue(link.getWeight().equals(weight));
    }

    session.close();
  }

  @AfterClass
  public static void deinit() {
    if (DRIVER != null) {
      Session session = DRIVER.session();
      Neo4JManager.empyting(session);
      session.close();
      DRIVER.close();
    }
  }

  @Test
  @Ignore
  public void test_path_same_write() throws Exception {
    Session session = DRIVER.session();

    long start = System.currentTimeMillis();
    for (int i = 0; i < 100000; i++) {
      boolean exists = Neo4JManager.existsPath(session, 1, 7);
      Assert.assertTrue(exists);
    }
    long end = System.currentTimeMillis();
    System.out.format("Same Write: %d\n", end-start);

    session.close();
  }

  @Test
  @Ignore
  public void test_path_same_read() throws Exception {
    Session session = DRIVER.session(AccessMode.READ);



    long start = System.currentTimeMillis();
    for (int i = 0; i < 100000; i++) {
      boolean exists = Neo4JManager.existsPath(session, 1, 7);
      Assert.assertTrue(exists);
    }
    long end = System.currentTimeMillis();
    System.out.format("Same Read: %d\n", end-start);

    session.close();
  }

  @Test
  @Ignore
  public void test_path_distinct_read() throws Exception {
    long start = System.currentTimeMillis();
    for (int i = 0; i < 100000; i++) {
      Session session = DRIVER.session(AccessMode.READ);
      boolean exists = Neo4JManager.existsPath(session, 1, 7);
      Assert.assertTrue(exists);
      session.close();
    }
    long end = System.currentTimeMillis();
    System.out.format("Distinct Read: %d\n", end-start);
  }
}
