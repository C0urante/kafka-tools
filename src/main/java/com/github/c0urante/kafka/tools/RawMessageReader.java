/*
     Copyright © 2020 - 2020 Chris Egerton <fearthecellos@gmail.com>
     This work is free. You can redistribute it and/or modify it under the
     terms of the Do What The Fuck You Want To Public License, Version 2,
     as published by Sam Hocevar. See the LICENSE file for more details.
*/

package com.github.c0urante.kafka.tools;

import kafka.common.MessageReader;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RawMessageReader implements MessageReader {

    public static final int DEFAULT_MAX_MESSAGE_SIZE = 1024;
    public static final String TOPIC_CONFIG = "topic";
    public static final String MAX_MESSAGE_SIZE_CONFIG = "max.message.size";

    private InputStream inputStream;
    private String topic;
    private byte[] buffer;
  
    @Override
    public void init(InputStream inputStream, Properties props) {
        this.inputStream = inputStream;
        this.topic = props.getProperty(TOPIC_CONFIG);

        String maxMessageSize = props.getProperty(MAX_MESSAGE_SIZE_CONFIG);
        if (maxMessageSize != null) {
            int bufferSize;
            try {
                bufferSize = Integer.parseInt(maxMessageSize);
            } catch (NumberFormatException e) {
                throw new RuntimeException(String.format(
                    "Invalid value '%s' for '%s' property; must be a positive integer",
                    maxMessageSize,
                    MAX_MESSAGE_SIZE_CONFIG
                ));
            }
            buffer = new byte[bufferSize];
        } else {
            buffer = new byte[DEFAULT_MAX_MESSAGE_SIZE];
        }
    }

    // Not sure if this is ever going to be called in parallel, but it's definitely not thread-safe,
    // so make method synchronized to avoid fights between threads over the byte buffer
    @Override
    public synchronized ProducerRecord<byte[], byte[]> readMessage() {
        int bytesRead;
        try {
            bytesRead = inputStream.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading from input stream", e);
        }

        if (bytesRead == -1) {
          return null;
        }

        byte[] value;
        if (bytesRead == buffer.length) {
            value = buffer;
        } else {
            value = new byte[bytesRead];
            System.arraycopy(buffer, 0, value, 0, bytesRead);
        }
        return new ProducerRecord<>(topic, value);
    }
  
    @Override
    public void close() {
        topic = null;
        buffer = null;
        try {
            inputStream.close();
        } catch (IOException e) {
          throw new RuntimeException("Failed to close input stream", e);
        }
    }
}
