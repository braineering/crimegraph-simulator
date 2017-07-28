/*
  The MIT License (MIT)

  Copyright (c) 2017 Giacomo Marciani

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

/**
 * All Neo4J queries.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class Neo4JQueries {

  /**
   * Query to create a new general link.
   */
  public static final String SAVE_LINK_MINED_GENERAL =
      "MERGE (u1:Person {id:{src}}) " +
          "MERGE (u2:Person {id:{dst}}) " +
          "MERGE (u1)-[r:%s]-(u2) " +
          "ON CREATE SET r.weight={weight},r.created=timestamp(),r.updated=r.created " +
          "ON MATCH SET r.weight={weight},r.updated=timestamp()";

  /**
   * Query to create a new real link.
   */
  public static final String SAVE_LINK_REAL =
      "MERGE (u1:Person {id:{src}}) " +
          "MERGE (u2:Person {id:{dst}}) " +
          "MERGE (u1)-[r:REAL]-(u2) " +
          "ON CREATE SET r.weight={weight},r.created=timestamp(),r.updated=r.created " +
          "ON MATCH SET r.weight={weight},r.updated=timestamp() " +
          "WITH u1,u2 " +
          "MATCH (u1)-[r2]-(u2) " +
          "WHERE NOT type(r2) = 'REAL' " +
          "DELETE r2";

  /**
   * Query to create a new real link (AVERAGE).
   */
  public static final String SAVE_LINK_REAL_AVERAGE =
      "MERGE (u1:Person {id:{src}}) " +
          "MERGE (u2:Person {id:{dst}}) " +
          "MERGE (u1)-[r:REAL]-(u2) " +
          "ON CREATE SET r.weight={weight},r.num=1,r.created=timestamp(),r.updated=r.created " +
          "ON MATCH SET r.weight=(r.weight*r.num+{weight})/(r.num+1),r.num=r.num+1,r.updated=timestamp() " +
          "WITH u1,u2 " +
          "MATCH (u1)-[r2]-(u2) " +
          "WHERE NOT type(r2) = 'REAL' " +
          "DELETE r2";

  /**
   * Query to create a new real link (EWMA).
   */
  public static final String SAVE_LINK_REAL_EWMA =
      "MERGE (u1:Person {id:{src}}) " +
          "MERGE (u2:Person {id:{dst}}) " +
          "MERGE (u1)-[r:REAL]-(u2) " +
          "ON CREATE SET r.weight={weight},r.num=1,r.created=timestamp(),r.updated=r.created " +
          "ON MATCH SET r.weight=({weight}*{ewma}+r.weight*(1-{ewma})),r.num=r.num+1,r.updated=timestamp() " +
          "WITH u1,u2 " +
          "MATCH (u1)-[r2]-(u2) " +
          "WHERE NOT type(r2) = 'REAL' " +
          "DELETE r2";

  /**
   * Query to remove a link.
   */
  public static final String REMOVE_LINK =
      "MATCH (x:Person {id:{x}})-[r]-(y:Person {id:{y}}) " +
          "WHERE type(r) = '{type}' " +
          "DELETE r";

  /**
   * Query to match real link.
   */
  public static final String MATCH_GENERAL =
      "MATCH (u1:Person {id:{src}})-[r:%s {weight:{weight}}]-(u2:Person {id:{dst}}) " +
          "RETURN r IS NOT NULL AS exists";

  /**
   * Query to match real link.
   */
  public static final String MATCH_REAL =
      "MATCH (u1:Person {id:{src}})-[r:REAL {weight:{weight}}]-(u2:Person {id:{dst}}) " +
          "RETURN r IS NOT NULL AS exists";

  /**
   * Query to match real path.
   */
  public static final String MATCH_PATH_REAL =
      "OPTIONAL MATCH (u1:Person {id:{src}})-[r:REAL*]-(u2:Person {id:{dst}}) " +
          "RETURN r IS NOT NULL AS exists";

  /**
   * Query to count links.
   */
  public static final String COUNT_LINKS =
      "MATCH ()-[r]->() RETURN COUNT(r) AS numlinks";

  /**
   * Query to remove all nodes on Neo4J
   */
  public static final String EMPYTING =
      "MATCH (n:Person) DETACH DELETE n";

}
