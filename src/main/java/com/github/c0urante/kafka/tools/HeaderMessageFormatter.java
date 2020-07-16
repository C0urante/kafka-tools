/*
     Copyright © 2020 - 2020 Chris Egerton <fearthecellos@gmail.com>
     This work is free. You can redistribute it and/or modify it under the
     terms of the Do What The Fuck You Want To Public License, Version 2,
     as published by Sam Hocevar. See the LICENSE file for more details.
*/

package com.github.c0urante.kafka.tools;

import kafka.common.MessageFormatter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

public class HeaderMessageFormatter implements MessageFormatter {

  private String outputDirectory;

  @Override
  public void init(Properties props) {
    outputDirectory = props.getProperty("output.directory", ".");
  }

  @Override
  public void writeTo(ConsumerRecord<byte[], byte[]> consumerRecord, PrintStream outputStream) {
    // Ignore the output stream. We're not writing to it. We're writing each header to its own file.
    String filenamePrefix = String.format("%s/%s-%s-%s-",
        outputDirectory, consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
    int headerNumber = 0;
    for (Header header : consumerRecord.headers()) {
      String headerKeyFile = filenamePrefix + headerNumber + "-header-key";
      String headerValueFile = filenamePrefix + headerNumber + "-header-value";
      
      try (FileOutputStream headerKeyOutput = new FileOutputStream(headerKeyFile)) {
        headerKeyOutput.write(header.key().getBytes());
      } catch (IOException e) {
        throw new RuntimeException("Error while writing to file " + headerKeyFile, e);
      }

      try (FileOutputStream headerValueOutput = new FileOutputStream(headerValueFile)) {
        headerValueOutput.write(header.value());
      } catch (IOException e) {
        throw new RuntimeException("Error while writing to file " + headerKeyFile, e);
      }

      headerNumber++;
    }
  }

  @Override
  public void close() {
  }
}
