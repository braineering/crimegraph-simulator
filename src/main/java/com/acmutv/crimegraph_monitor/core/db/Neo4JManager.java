/*
  The MIT License (MIT)

  Copyright (c) 2017 Giacomo Marciani and Michele Porretta

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.*;

import static com.acmutv.crimegraph_monitor.core.db.Neo4JQueries.*;
import static com.acmutv.crimegraph_monitor.core.link.LinkType.REAL;
import static org.neo4j.driver.v1.Values.parameters;

/**
 * Collections of NEO4J useful queries.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class Neo4JManager {

  private static final Logger LOGGER = LogManager.getLogger(Neo4JManager.class);

  /**
   * Opens a NEO4J connection.
   * @param dbconf the configuration to connect to Neo4J.
   * @return a open NEO4J driver.
   */
  public static Driver open(DbConfiguration dbconf) {
    AuthToken auth = AuthTokens.basic(dbconf.getUsername(), dbconf.getPassword());
    Config config = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE ).toConfig();
    return GraphDatabase.driver(dbconf.getHostname(), auth, config);
  }

  /**
   * Opens a NEO4J connection.
   * @param hostname the instance hostname.
   * @param username the username for the authentication.
   * @param password the password for the authentication.
   * @return a open NEO4J driver.
   */
  public static Driver open(String hostname, String username, String password) {
    AuthToken auth = AuthTokens.basic(username, password);
    Config config = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE ).toConfig();
    return GraphDatabase.driver(hostname, auth, config);
  }

  /**
   * Closes the NEO4J connection.
   * @param session the NEO4J session.
   * @param driver the NEO4J driver.
   */
  public static void close(Session session, Driver driver) {
    if (session != null && session.isOpen()) {
      session.close();
    }
    if (driver != null) {
      driver.close();
    }
  }

  /**
   * Saves a new link.
   * @param session the NEO4J open session.
   * @param link the link to save.
   */
  public static void save(Session session, Link link) {
    LOGGER.trace("Link {}", link);
    long src = link.getSrc();
    long dst = link.getDst();
    double weight = link.getWeight();
    LinkType type = link.getType();

    Value params = parameters("src", src, "dst", dst, "weight", weight);

    if (REAL.equals(type)) {
      session.run(SAVE_LINK_REAL_AVERAGE, params);
    } else {
      final String SAVE_LINK_MINED = String.format(SAVE_LINK_MINED_GENERAL, type);
      session.run(SAVE_LINK_MINED, params);
    }
  }

  /**
   * Saves a new link.
   * @param session the NEO4J open session.
   * @param link the link to save.
   * @param ewmaFactor the EWMA factor for recent observation.
   */
  public static void save(Session session, Link link, double ewmaFactor) {
    long src = link.getSrc();
    long dst = link.getDst();
    double weight = link.getWeight();
    LinkType type = link.getType();

    Value params = parameters("src", src, "dst", dst, "weight", weight, "ewma", ewmaFactor);

    if (REAL.equals(type)) {
      session.run(SAVE_LINK_REAL_EWMA, params);
    } else {
      final String SAVE_LINK_MINED = String.format(SAVE_LINK_MINED_GENERAL, type);
      session.run(SAVE_LINK_MINED, params);
    }
  }

  /**
   * Checks a real link.
   * @param session the NEO4J open session.
   * @param link the link to checkDatasetOnDb.
   * @return true, if the link exists; false, otherwise.
   */
  public static boolean existsReal(Session session, Link link) {
    long src = link.getSrc();
    long dst = link.getDst();
    double weight = link.getWeight();

    boolean exists = false;

    Value params = parameters("src", src, "dst", dst, "weight", weight);

    StatementResult result = session.run(MATCH_REAL, params);
    while (result.hasNext()) {
      Record rec = result.next();
      exists = rec.get("exists").asBoolean();
    }

    return exists;
  }

  /**
   * Checks a real link.
   * @param session the NEO4J open session.
   * @param link the link to checkDatasetOnDb.
   * @return true, if the link exists; false, otherwise.
   */
  public static boolean exists(Session session, Link link) {
    long src = link.getSrc();
    long dst = link.getDst();
    double weight = link.getWeight();
    LinkType type = link.getType();

    boolean exists = false;

    String query = String.format(MATCH_GENERAL, type);

    Value params = parameters("src", src, "dst", dst, "weight", weight);

    StatementResult result = session.run(query, params);
    if (result.hasNext()) {
      Record rec = result.next();
      exists = rec.get("exists").asBoolean();
    }

    return exists;
  }

  /**
   * Checks a real path between nodes.
   * @param session the NEO4J open session.
   * @param src the source node.
   * @param dst the destination node.
   * @return true, if the real path exists; false, otherwise.
   */
  public static boolean existsPath(Session session, long src, long dst) {
    boolean exists = false;

    Value params = parameters("src", src, "dst", dst);

    StatementResult result = session.run(MATCH_PATH_REAL, params);
    if (result.hasNext()) {
      Record rec = result.next();
      exists = rec.get("exists").asBoolean();
    }

    return exists;
  }

  /**
   * Counts all the links.
   * @param session the NEO4J open session.
   * @return the number of links.
   */
  public static long countLinks(Session session) {
    long numlinks = 0;
    StatementResult result = session.run(COUNT_LINKS);
    if (result.hasNext()) {
      Record rec = result.next();
      numlinks = rec.get("numlinks").asLong();
    }

    return numlinks;
  }

  /**
   * Removes a link.
   * @param x the id of the first node.
   * @param y the id of the second node.
   * @param type the type of link.
   */
  public static void remove(Session session, long x, long y, LinkType type) {
    Value params = parameters("x", x, "y", y, "type", type.name());
    session.run(REMOVE_LINK, params);
  }

  /**
   * Empyting of Neo4J
   * @param dbconf the configuration to connect to Neo4J.
   */
  public static void empyting(DbConfiguration dbconf) {
    String hostname = dbconf.getHostname();
    String username = dbconf.getUsername();
    String password = dbconf.getPassword();
    Driver driver = Neo4JManager.open(hostname, username, password);
    Session session = driver.session();
    session.run(EMPYTING);
    Neo4JManager.close(session, driver);
  }

  /**
   * Empyting of Neo4J
   * @param session the Neo4J session.
   */
  public static void empyting(Session session) {
    session.run(EMPYTING);
  }

}
