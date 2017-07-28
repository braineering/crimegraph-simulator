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
package com.acmutv.crimegraph_monitor.core.kafka;

import com.acmutv.crimegraph_monitor.core.link.Link;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

/**
 * A simple Kafka consumer.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class StringKafkaConsumer {

  private static final Logger LOGGER = LogManager.getLogger(StringKafkaConsumer.class);

  private Consumer<String,String> consumer;

  public StringKafkaConsumer(String bootstrapServers, String groupId) {
    Properties props = new Properties();
    props.put("bootstrap.servers", bootstrapServers);
    props.put("group.id", groupId);
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.deserializer", StringDeserializer.class.getName());
    props.put("value.deserializer", StringDeserializer.class.getName());
    this.consumer = new KafkaConsumer<>(props);
  }

  public Link receive(String topic) {
    this.consumer.subscribe(Collections.singletonList(topic));
    String message = null;
    try {
      ConsumerRecords<String, String> records = this.consumer.poll(3000);
      Iterator<ConsumerRecord<String, String>> iter = records.iterator();
      if (iter.hasNext()) message = iter.next().value();
    } finally {
      this.consumer.close();
    }

    Link link;
    try {
      link = Link.valueOf(message);
    } catch (IllegalArgumentException exc) {
      LOGGER.warn("Malformed link: {}", message);
      link = null;
    }

    return link;
  }

  public void close() {
    this.consumer.close();
  }
}
