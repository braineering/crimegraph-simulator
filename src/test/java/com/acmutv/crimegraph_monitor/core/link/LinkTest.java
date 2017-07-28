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

package com.acmutv.crimegraph_monitor.core.link;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * JUnit test suite for {@link Link}.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see Link
 */
public class LinkTest {

  private static final Logger LOGGER = LogManager.getLogger(LinkTest.class);

  /**
   * Tests serialization of {@link Link}.
   */
  @Test
  public void test_serialize() throws Exception {
    List<Link> links = new LinkedList<>();

    links.add(new Link(1,2,0.0));
    for (LinkType type : LinkType.values()) {
      links.add(new Link(1,2,123.123, type));
    }

    for (Link expected : links) {
      LOGGER.debug("Link serialized: " + expected);
      String line = expected.toString();
      Link actual;
      try {
        actual = Link.valueOf(line);
      } catch (IllegalArgumentException exc) {
        LOGGER.warn("Malformed link: {}", line);
        actual = null;
      }
      Assert.assertEquals(expected, actual);
    }
  }
}
