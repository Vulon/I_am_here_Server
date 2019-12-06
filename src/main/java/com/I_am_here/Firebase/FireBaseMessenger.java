package com.I_am_here.Firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class FireBaseMessenger {
    private static final String FILE_DIR = "src/resources/service-account.json";

    private static final String PROJECT_ID = "i-am-here-2019";
    private static final String BASE_URL = "https://fcm.googleapis.com";
    private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };
    private static final String MESSAGE_KEY = "message";


    public void init() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(FILE_DIR)))
                .build();

        FirebaseApp.initializeApp(options);
    }

    /**
     * Send notification message to FCM for delivery to registered devices.
     *
     * @throws FirebaseMessagingException .
     */
    public void sendNotification(String messageTitle, String messageBody, ArrayList<String> topicIds) throws FirebaseMessagingException {
        AndroidConfig notificationConfig =
                AndroidConfig.builder()
                        .setTtl(3600 * 1000 * 24) // 1 day in milliseconds
                        .setPriority(AndroidConfig.Priority.NORMAL)
                        .setNotification(AndroidNotification.builder()
                                .setTitle(messageTitle)
                                .setBody(messageBody)
//                                .setIcon("stock_ticker_update")
//                                .setColor("#f45342")
                                .build())
                        .build();

        ArrayList<Message> messages = new ArrayList<>();
        for (String topic : topicIds) {
            messages.add(
                    Message.builder()
                            .setAndroidConfig(notificationConfig)
                            .setTopic(topic)
                            .build());
        }

        BatchResponse response = FirebaseMessaging.getInstance().sendAll(messages);
        System.out.println(response.getSuccessCount() + " messages were sent successfully");
    }

    /**
     * Subscribe user to topic using device registration token.
     *
     * @param registrationToken String these registration token come from the client FCM SDKs.
     * @param topicId String of the topic to subscribe to.
     * @throws FirebaseMessagingException .
     */
    public void subscribeToTopic(String registrationToken, String topicId) throws FirebaseMessagingException {
        // Subscribe the devices corresponding to the registration tokens to the topic.
        TopicManagementResponse response =
                FirebaseMessaging.getInstance().subscribeToTopic(
                        Collections.singletonList(registrationToken), topicId);

        System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");
    }


    /**
     * UnSubscribe user from topic using device registration token.
     *
     * @param registrationToken String these registration token come from the client FCM SDKs.
     * @param topicId String of the topic to unsubscribe from.
     * @throws FirebaseMessagingException .
     */
    public void unsubscribeFromTopic(String registrationToken, String topicId) throws FirebaseMessagingException {
        // Unsubscribe the devices corresponding to the registration tokens from the topic.
        TopicManagementResponse response =
                FirebaseMessaging.getInstance().unsubscribeFromTopic(
                        Collections.singletonList(registrationToken), topicId);

        System.out.println(response.getSuccessCount() + " tokens were unsubscribed successfully");
    }
}