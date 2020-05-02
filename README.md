# Kafka Tools

A collection of lightweight but useful utilities for interacting with [Apache Kafka].

1. [Kafka Binary Console Tools](#kafka-binary-console-tools)
1. [Installation](#installation)
1. [Issue Tracking](#issue-tracking)
1. [TODO](#todo)


## Kafka Binary Console Tools

### Kafka Binary Producer

`kafka-binary-producer` is a small wrapper around the `kafka-console-producer` tool that sends input
as-is to Kafka without parsing or altering it at all. This is useful for processing binary data that
may contain bytes with values like 10 or 13 that would get interpreted as newlines and discarded by
the console producer normally.

Every message written to Kafka will have a null key and a raw byte array containing exactly what was
read from the console.

#### Configuration

The property `max.message.size` can be specified in order to control the maximum size of the value
of each message. The default value is 1024;

### Kafka Binary Consumer

`kafka-binary-consumer` is a small wrapper around the `kafka-console-consumer` tool that writes the
data it receives from Kafka as-is to the console without parsing or altering it at all. This is
useful for processing binary data that may contain bytes with values like 10 or 13 that would get
interpreted as newlines and discarded or altered by the console consumer normally.

Only the value of each message is written. Properties like `print.key` are not yet supported.

#### Configuration

This tool has no extra configurability beyond what is already provided by `kafka-console-consumer`.

### Quickstart

Assumptions:

- Maven 3+ is installed
- Zookeeper is running and listening on localhost:2181
- Kafka is running and listening on localhost:9092
- Current directory is the root of the repository

```bash
# Build the project
mvn clean install

# Create the topic that the producer will write to (important: only need one partition, otherwise data may be read out of order)
kafka-topics --zookeeper localhost:2181 --create --topic music-mp3 --partitions 1 --replication-factor 1

# Run the binary producer on a test file
bin/kafka-binary-producer --broker-list localhost:9092 --topic music-mp3 < src/test/resources/audio/music.mp3

# Run the binary consumer on that topic and write it to a file
bin/kafka-binary-consumer --bootstrap-server localhost:9092 --topic music-mp3 --from-beginning --timeout-ms 1000 > test.mp3

# Verify that the contents are identical
diff test.mp3 src/test/resources/audio/music.mp3

# (Optional but fun) play the audio file! (requires SoX)
play test.mp3
```

## Installation

### Local build

Build the project with:

```bash
mvn clean package
```

and then add the `bin/` directory onto your `PATH`:

```bash
export PATH="$PATH:$PWD/bin/"
```

### Onto an existing Kafka deployment

TODO

### As a standalone artifact

TODO

## Issue Tracking

Issues are tracked on GitHub. If there's a problem you're running into
with the connector or a feature missing that you'd like to see, please
open an issue.

If there's a small bug or typo that you'd like to fix, feel free to open
a PR without filing an issue first and tag @C0urante for review.

## TODO

- [ ] Publish first release
- [ ] Support installation onto an existing Kafka deployment
- [ ] Support deployment as a completely sufficient standalone tool
- [ ] Tests :(
- [ ] Make it easier to consume from/write to a file without requiring shell redirects

PRs welcome and encouraged!

[Kafka Connect]: https://docs.confluent.io/current/connect
[Apache Kafka]: https://kafka.apache.org
[Confluent Hub]: https://confluent.io/hub
