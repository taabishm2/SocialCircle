package com.socialcircle.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.socialcircle.dao.ConnectDao;
import com.socialcircle.dao.ContactDao;
import com.socialcircle.dao.UserDao;
import com.socialcircle.entity.Connect;
import com.socialcircle.entity.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

@Service
public class CronAPI {
    @Autowired
    private ConnectDao connectDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ContactDao contactDao;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    @Transactional(rollbackOn = ApiException.class)
    public void generateSuggestions() throws UnirestException {
        List<Contact> contacts = contactDao.selectAll();
        Map<Long, List<Contact>> contactMap = contacts.stream()
                .collect(Collectors.groupingBy(i -> i.getUserAId(), Collectors.toList()));

        for (Map.Entry<Long, List<Contact>> entry : contactMap.entrySet()) {
            Long userId = entry.getKey();
            for (Contact contact : entry.getValue()) {
                Long otherUserId = contact.getUserBId();

                Long currentProgress = contact.getProgress();
                Long nextProgress = currentProgress + 1;

                if (Math.floor(curveSum(nextProgress, contact)) - Math.floor(curveSum(currentProgress, contact)) < 1) {
                    contact.setProgress(nextProgress);
                    continue;
                }

                Connect pendingConnect = getPendingConnect(userId, otherUserId);

                List<Connect> oldConnects = connectDao.getByUsers(userId, otherUserId);
                String oldNotes = String.join(", ", oldConnects.stream().map(i -> i.getNotes())
                        .filter(i -> Objects.nonNull(i)).collect(Collectors.toList()));

                if (!Objects.isNull(pendingConnect)) {
                    if (requiresRepeatPing(currentProgress, contact, pendingConnect))
                        createConnectPing(userId, otherUserId, false, pendingConnect, oldNotes);
                } else {
                    createConnectPing(userId, otherUserId, true, null, oldNotes);
                }
            }
        }
    }

    private void createConnectPing(Long userId, Long otherUserId, boolean createConnect, Connect pendingConnect, String notes) throws UnirestException {
        String outNotes = "";
        if (!createConnect) {
            pendingConnect.setNotes(notes);
            outNotes = notes;
            pendingConnect.setCreatedAt(ZonedDateTime.now());
        } else {
            Connect connect = new Connect();
            connect.setIsSuggestion(true);
            connect.setSourceUserId(Math.min(userId, otherUserId));
            connect.setDestinationUserId(Math.max(userId, otherUserId));
            outNotes = notes;
            connectDao.persist(connect);
        }

        sendViaGoogleCalendar(userDao.select(userId).getEmail(), userDao.select(otherUserId).getEmail(), outNotes);
        // TODO: Notify via Twilio
    }

    public static String formatZonedDateTime() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'");
        String formattedNow = now.format(formatter);
        return formattedNow;
    }

    private void sendViaGoogleCalendar(String emailA, String emailB, String previousNotes) throws UnirestException {
        String gptResponse = getGptResponse(previousNotes);
        String calendarBody = "{" +
                "\"summary\": " +
                "\"SocialCircle Invite (" + emailA + "," + emailB + ")\"," +
                "\"location\": " +
                "\"Madison, WI\"," +
                "\"description\": " +
                "\"" + gptResponse + "\"," +
                "\"attendees\": " +
                "[" +
                "{\"email\": \"" + emailA + "\"}," +
                "{\"email\": \"" + emailB + "\"}]," +
                "\"start\": {\"dateTime\": \"" + formatZonedDateTime() + "20:00:00-06:00\"}," +
                "\"end\": {\"dateTime\": \"" + formatZonedDateTime() + "21:00:00-06:00\"}}";

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("https://www.googleapis.com/calendar/v3/calendars/7d5544971839dffa3ca336332a639e7ca3955b30c130d5ae892e973b1be6fba2%40group.calendar.google.com/events?key=AIzaSyDTeYP9jkORRRw0LOEhqcAlJVEYY8R-Dbk")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ya29.a0AVvZVsr1nVm7-KXADMhXKSDaa3xTZEGQklmIUwK-wp1taHOe8doYm6td72nGcSJp0eAUbTCe8vpJ5hAmM-PzVDYglugdIQl0io3QWlDCG4uewNgJkiN3Sw7z4Mg5jmU_1TapgF_AJyI9nSjZkwYP0DsdCisuaCgYKAfUSARISFQGbdwaIefXpe4Y3FYRGlyS5QzuQNg0163")
                .body(calendarBody)
                .asString();
    }

    private boolean requiresRepeatPing(Long progress, Contact contact, Connect pendingConnect) {
        double daysBetweenPings = 1.0 / curve(progress, contact);
        return daysBetween(pendingConnect.getCreatedAt(), ZonedDateTime.now(), ChronoUnit.DAYS) >= daysBetweenPings;
    }

    private Connect getPendingConnect(Long userId, Long otherUserId) {
        return connectDao.getPendingByUsers(userId, otherUserId);
    }

    private double curveSum(Long maxProgress, Contact contact) {
        Double curveSum = 0d;
        for (long i = 0; i <= maxProgress; i++) {
            curveSum += curve(i, contact);
        }
        return curveSum;
    }

    private double curve(Long progress, Contact contact) {
        Double m = ((1.0 / Double.valueOf(contact.getTargetFrequency())) - (1.0 / Double.valueOf(contact.getInitialFrequency()))) / Double.valueOf(contact.getTimeframe());
        return (m * progress) + (1.0 / contact.getInitialFrequency());
    }

    static long daysBetween(ZonedDateTime d1, ZonedDateTime d2, ChronoUnit unit) {
        return unit.between(d1, d2);
    }

    public static void appendToFile() {
        String filePath = "./timestamps.txt"; // Set file path as current directory + file name
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime now = LocalDateTime.now();
            String timestamp = formatter.format(now);
            writer.write(timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getGptResponse(String notesString) throws UnirestException {
        String url = "https://api.openai.com/v1/completions";
        String authorizationHeader = "Bearer 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";

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "text-davinci-003");
        requestBody.put("prompt", "Write a few talking points with a friend based on the following notes from previous conversations: " + notesString + ". If the string is empty suggest some general talking points. Make the points sound causal and super friendly.");
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 256);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0);
        requestBody.put("presence_penalty", 0);

        HttpResponse<JsonNode> response = Unirest.post(url)
                .header("Authorization", authorizationHeader)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .asJson();

        int statusCode = response.getStatus();
        JSONObject responseBody = response.getBody().getObject();
        //String responseStr = response.getBody().getObject().get("choices").myArrayList.get(0).get("text");

        String fullResponse = response.getBody().getObject().get("choices").toString();
        return fullResponse.substring(47, fullResponse.length() - 19);
    }

}
