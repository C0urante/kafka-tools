/*
     Copyright © 2020 - 2020 Chris Egerton <fearthecellos@gmail.com>
     This work is free. You can redistribute it and/or modify it under the
     terms of the Do What The Fuck You Want To Public License, Version 2,
     as published by Sam Hocevar. See the LICENSE file for more details.
*/

package com.github.c0urante.kafka.tools;

import kafka.common.MessageFormatter;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

public class RawMessageFormatter implements MessageFormatter {

  @Override
  public void init(Properties props) {
  }

  @Override
  public void writeTo(ConsumerRecord<byte[], byte[]> consumerRecord, PrintStream outputStream) {
    try {
      outputStream.write(consumerRecord.value());
    } catch (IOException e) {
      throw new RuntimeException("Error while writing to output stream", e);
    }
  }

  @Override
  public void close() {
  }
}
