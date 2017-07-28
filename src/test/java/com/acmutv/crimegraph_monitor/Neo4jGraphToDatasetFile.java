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
  import org.junit.Test;
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

  import org.neo4j.driver.v1.*;

  import com.acmutv.crimegraph_monitor.core.tuple.Link;

  import static com.acmutv.crimegraph_monitor.Common.HOSTNAME;
  import static com.acmutv.crimegraph_monitor.Common.PASSWORD;
  import static com.acmutv.crimegraph_monitor.Common.USERNAME;

/**
 * JUnit test for rebuild a dataset file from Neo4j
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see Link
 */
/*public class Neo4jGraphToDatasetFile {

  private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jGraphToDatasetFile.class);

  private static Driver DRIVER;

  private static final String GETLINKS =
          "MATCH (a:Person)-[r]->(b:Person) " +
                  "RETURN a.id as src, b.id as dst, r.weight as weight";

  @Test
  public void getData() throws Exception {

    DbConfiguration dbconf = new DbConfiguration(HOSTNAME, USERNAME, PASSWORD);
    DRIVER = Neo4JManager.open(dbconf);
    Session session = DRIVER.session();

    StatementResult result = session.run(GETLINKS);

    List<Link> data = new ArrayList<>();

    while (result.hasNext()) {
      Record rec = result.next();
      Long src = rec.get("src").asLong();
      Long dst = rec.get("dst").asLong();
      Double weight = rec.get("weight").asDouble();
      Link link = new Link(src,dst,weight);
      data.add(link);
    }

    Path path = FileSystems.getDefault().getPath("data/criminal_dataset.data");

    if (!Files.isDirectory(path.getParent())) {
      Files.createDirectories(path.getParent());
    }

    Charset charset = Charset.defaultCharset();
    try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
      for (Link link : data) {
        writer.append(link.toString()).append("\n");
      }
    } catch (IOException exc) {
      LOGGER.error(exc.getMessage());
    }
  }
}*/
