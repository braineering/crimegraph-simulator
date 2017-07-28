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

/*package com.acmutv.crimegraph_monitor;

import com.acmutv.crimegraph_monitor.core.db.DbConfiguration;
import com.acmutv.crimegraph_monitor.core.db.Neo4JManager;
import com.acmutv.crimegraph_monitor.core.tuple.Link;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.acmutv.crimegraph_monitor.Common.*;

/**
 * JUnit test for loading data on Neo4j
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see Link
 */
/*public class SimpleLoaderLinkToNeo4j {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoaderLinkToNeo4j.class);

  private static Driver DRIVER;

  @Test
  public void pushData() throws Exception {

    DbConfiguration dbconf = new DbConfiguration(HOSTNAME, USERNAME, PASSWORD);
    DRIVER = Neo4JManager.open(dbconf);
    Session session = DRIVER.session();

    Path path = FileSystems.getDefault().getPath("data/example.data");

    BufferedReader reader = Files.newBufferedReader(path);

    while (reader.ready()) {
      Link link = Link.valueOf(reader.readLine().toString());
      Neo4JManager.save(session,link);
    }
  }
}*/
