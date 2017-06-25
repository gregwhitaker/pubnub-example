package com.github.gregwhitaker.pubnub.example;

import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {
        String subscribeKey = System.getProperty("subscribeKey");
        if (subscribeKey == null || subscribeKey.isEmpty()) {
            throw new IllegalArgumentException("System property 'subscribeKey' is required!");
        }

        String publishKey = System.getProperty("publishKey");
        if (publishKey == null || publishKey.isEmpty()) {
            throw new IllegalArgumentException("System property 'publishKey' is required!");
        }

        // Create configuration to determine who is connecting
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(subscribeKey);
        pnConfiguration.setPublishKey(publishKey);

        PubNub pubnub = new PubNub(pnConfiguration);

        // Create Listener to Handle Messages
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                LOG.info(message.getMessage().toString());
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        // Subscribe to message on the "test-channel" channel
        pubnub.subscribe()
                .channels(Arrays.asList("test-channel"))
                .execute();

        for (int i = 1; i <= 10; i++) {
            JsonObject message = new JsonObject();
            message.addProperty("text", "Test Message: " + i);

            // Publish messages to the "test-channel" channel
            pubnub.publish()
                    .message(message)
                    .channel("test-channel")
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            if (status.isError()) {
                                LOG.error(status.getErrorData().getInformation());
                            } else {
                                LOG.info("Published Message: " + message.toString());
                            }
                        }
                    });
        }
    }
}
