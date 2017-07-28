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

package com.acmutv.crimegraph_monitor.core.datagen;

import com.acmutv.crimegraph_monitor.core.CoreController;
import com.acmutv.crimegraph_monitor.core.link.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.nio.file.*;
import java.util.List;

/**
 * Utility for the original dataset generation.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see Link
 */
public class DatagenRandomTest {

  private static final Logger LOGGER = LogManager.getLogger(DatagenRandomTest.class);

  private static final int NUM_NODES = 50;

  private static final int NUM_LINKS = 100;

  private static final double MIN_WEIGHT = 1.0;

  private static final double MAX_WEIGHT = 100.0;

  private static final Path TESTDIR = FileSystems.getDefault().getPath("data/test/random");

  /**
   * Generates a simple random dataset.
   */
  @Test
  public void simple() throws Exception {
    final String datasetName = "simple.data";
    final Path path = Paths.get(TESTDIR.toString(), datasetName);

    if (!Files.isDirectory(path.getParent())) {
      Files.createDirectories(path.getParent());
    }

    List<Link> data = CoreController.randomSimple(NUM_NODES, NUM_LINKS, MIN_WEIGHT, MAX_WEIGHT);
    CoreController.writeDataset(path, data);
  }

  /**
   * Generates a simple circular dataset.
   */
  @Test
  public void circular() throws Exception {
    final String datasetName = "circular.data";
    final Path path = Paths.get(TESTDIR.toString(), datasetName);

    if (!Files.isDirectory(path.getParent())) {
      Files.createDirectories(path.getParent());
    }

    List<Link> data = CoreController.randomCircular(NUM_NODES, NUM_LINKS, MIN_WEIGHT, MAX_WEIGHT);
    CoreController.writeDataset(path, data);
  }
}
