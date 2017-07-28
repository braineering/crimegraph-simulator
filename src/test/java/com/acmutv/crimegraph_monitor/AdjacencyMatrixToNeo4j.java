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

package com.acmutv.crimegraph_monitor;

import com.acmutv.crimegraph_monitor.core.db.DbConfiguration;
import com.acmutv.crimegraph_monitor.core.link.Link;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.acmutv.crimegraph_monitor.Common.*;

/**
 * JUnit test for rebuild a dataset file from Neo4j
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see Link
 */
public class AdjacencyMatrixToNeo4j {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdjacencyMatrixToNeo4j.class);

  private static Driver DRIVER;

  @Test
  public void getDataFromMatrix() throws Exception {

    DbConfiguration dbconf = new DbConfiguration(HOSTNAME, USERNAME, PASSWORD);
    //DRIVER = Neo4JManager.open(dbconf);
    //Session session = DRIVER.session();

    Path source = FileSystems.getDefault().getPath("data/IR.data");

    /*int i;
    int j;
    int n = 182;

    BufferedReader input = new BufferedReader(new FileReader(source.toString()));

    for(i = 0; i< n; i++) {
      String line = input.readLine();
      String[] tokenizer = line.split(";");
      for (j = i+1; j < n; j++) {
        if (Integer.parseInt(tokenizer[j].toString()) == 1){
          int src = i;
          int dst = j;
          Link link = new Link(src, dst, 60.0);
          data.add(link);
        }
      }
    }*/

    BufferedReader reader = Files.newBufferedReader(source);
    List<Link> data = new ArrayList<>();

    boolean added = false;

    while (reader.ready()) {
      String[] tokenizer = reader.readLine().toString().split("\\s+");
      System.out.println("src: "+tokenizer[0].toString() + " dst: "+tokenizer[1].toString());
      if(Integer.parseInt(tokenizer[0].toString()) > Integer.parseInt(tokenizer[1].toString())) {
        String temp = tokenizer[1];
        tokenizer[1] = tokenizer[0];
        tokenizer[0] = temp;
      }

      Link link = Link.valueOf("("+tokenizer[0]+","+tokenizer[1]+",1.0)");

      if(data.size() >0) {
        added = false;
        for(int i = 0; i < data.size(); i++) {
          long dst = data.get(i).getDst();
          long src = data.get(i).getSrc();
          System.out.println("here");
          if(src == link.getSrc() && dst == link.getDst()) {
            double weight = data.get(i).getWeight()+1.0;
            data.get(i).setWeight(weight);
            added = true;
            break;
          }
        }
        if(added == false)
          data.add(link);
      }
      else
        data.add(link);
    }


    Path path = FileSystems.getDefault().getPath("data/IR_new.data");

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
}