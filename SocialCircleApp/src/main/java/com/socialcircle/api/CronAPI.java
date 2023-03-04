package com.socialcircle.api;

import com.socialcircle.config.security.SecurityUtil;
import com.socialcircle.dao.ConnectDao;
import com.socialcircle.dao.ContactDao;
import com.socialcircle.dao.UserDao;
import com.socialcircle.entity.Connect;
import com.socialcircle.entity.Contact;
import com.socialcircle.model.form.ConnectForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CronAPI {
    @Autowired
    private ConnectDao connectDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ContactDao contactDao;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void generateSuggestions() {
        List<Contact> contacts = contactDao.selectAll();
        Map<Long, List<Contact>> contactMap = contacts.stream()
                .collect(Collectors.groupingBy(i -> i.getUserAId(), Collectors.toList()));

        for (Map.Entry<Long, List<Contact>> entry : contactMap.entrySet()) {
            Long userId = entry.getKey();
            for (Contact contact : entry.getValue()) {
                Long otherUserId = contact.getUserBId();

                Long currentProgress = contact.getProgress();
                Long nextProgress = currentProgress + 1;

                if (curve(nextProgress, contact) - curve(currentProgress, contact) < 1) continue;

                Connect pendingConnect = getPendingConnect(userId, otherUserId);
                if (!Objects.isNull(pendingConnect)) {
                    if (requiresRepeatPing(currentProgress, contact, pendingConnect))
                        createConnectPing(userId, otherUserId, false, pendingConnect);
                } else {
                    createConnectPing(userId, otherUserId, true, null);
                    contact.setProgress(nextProgress);
                }
            }
        }
    }

    private void createConnectPing(Long userId, Long otherUserId, boolean createConnect, Connect pendingConnect) {
        // Add/update connect suggest in DB
        if (!createConnect) {
            pendingConnect.setCreatedAt(ZonedDateTime.now());
        } else {
            Connect connect = new Connect();
            connect.setIsSuggestion(true);
            connect.setSourceUserId(Math.min(userId, otherUserId));
            connect.setDestinationUserId(Math.max(userId, otherUserId));
            connectDao.persist(connect);
        }
        // TODO: Send via google calendar
        // TODO: Notify via Twilio
    }

    private boolean requiresRepeatPing(Long progress, Contact contact, Connect pendingConnect) {
        double daysBetweenPings = 1.0 / curve(progress, contact);
        return daysBetween(pendingConnect.getCreatedAt(), ZonedDateTime.now(), ChronoUnit.DAYS) >= daysBetweenPings;
    }

    private Connect getPendingConnect(Long userId, Long otherUserId) {
        return connectDao.getByUsers(userId, otherUserId, true);
    }

    private double curve(Long progress, Contact contact) {
        Long m = (contact.getTargetFrequency() - contact.getInitialFrequency()) / Long.valueOf(contact.getTimeframe());
        return (m * progress) + contact.getInitialFrequency();
    }

    public Connect add(ConnectForm form) throws ApiException {
        Connect connect = new Connect();
        connect.setSourceUserId(SecurityUtil.getPrincipal().getUserId());
        connect.setDestinationUserId(form.getConnectedWithUserId());
        connect.setConnectTime(form.getConnectTime());
        connect.setNotes(form.getNotes());
        connectDao.persist(connect);

        return connect;
    }

    static long daysBetween(ZonedDateTime d1, ZonedDateTime d2, ChronoUnit unit) {
        return unit.between(d1, d2);
    }

}
