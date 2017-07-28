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

package com.acmutv.crimegraph_monitor.ui;

import lombok.Getter;

/**
 * All types of CLI commands.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
@Getter
public enum Command {
  ANALYZE_DATASET       ("analyze_dataset"),
  DATAGEN_RND           ("datagen_rnd"),
  DATAGEN_LCC           ("datagen_lcc"),
  TRAINTEST             ("traintest"),
  CHECK                 ("check"),
  PUBLISH               ("publish"),
  SAVE                  ("save"),
  CHECK_DATASET_DB      ("check_dataset_db"),
  WAIT_STABILITY_DB     ("wait_stability_db"),
  EVALUATE              ("evaluate");

  private final String name;

  Command(final String name) {
    this.name = name;
  }

  public static Command fromString(String string) {
    for (Command command : Command.values()) {
      if (command.name.equalsIgnoreCase(string)) {
        return command;
      }
    }
    return null;
  }
}
