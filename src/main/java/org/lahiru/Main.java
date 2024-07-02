package org.lahiru;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final int NO_OF_EVENTS = 5000;
    public static void main(String[] args) throws IOException, InterruptedException {
        String xml = readFile("/appointment.xml");
        long sequenceNumber = Long.parseLong(Objects.requireNonNull(extractTagValue(xml, "Sequence_Number")));
        long nextSequenceNumber = sequenceNumber + 1;

        for (long newSequenceNumber = nextSequenceNumber; newSequenceNumber < nextSequenceNumber + NO_OF_EVENTS; newSequenceNumber++) {
            xml = xml.replace(Long.toString(newSequenceNumber - 1), Long.toString(newSequenceNumber)).replaceAll("(\\r\\n|\\r)+", "");
            LOGGER.info("Going to publish, SequenceNumber: {}, AppointmentId: {}", newSequenceNumber);
            publishWithErrorHandlerExample(xml);
        }
    }

    public static void publishWithErrorHandlerExample(String message) throws IOException, InterruptedException {
        String projectId = "your-gcp-project-id";
        String topicId = "your-pubsub-topic";
        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;

        try {
            publisher = Publisher.newBuilder(topicName).build();
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            ApiFuture<String> future = publisher.publish(pubsubMessage);

            ApiFutures.addCallback(
                    future,
                    new ApiFutureCallback<String>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            if (throwable instanceof ApiException) {
                                ApiException apiException = ((ApiException) throwable);
                                LOGGER.info(String.valueOf(apiException.getStatusCode().getCode()));
                                LOGGER.info(String.valueOf(apiException.isRetryable()));
                            }
                            LOGGER.info("Error publishing message : {}", message);
                        }

                        @Override
                        public void onSuccess(String messageId) {
                            LOGGER.info("Published message ID: {}", messageId);
                        }
                    },
                    MoreExecutors.directExecutor());

        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }

    private static String readFile(String filePath) throws IOException {
        String eventData = "";
        InputStream inputStream = Main.class.getResourceAsStream(filePath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                eventData = eventData + line;
            }
        }
        return eventData;
    }

    private static String extractTagValue(String xmlString, String tagName) {
        String patternString = "<" + tagName + ">(.*?)</" + tagName + ">";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(xmlString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}