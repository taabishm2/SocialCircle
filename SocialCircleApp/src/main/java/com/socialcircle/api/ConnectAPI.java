package com.socialcircle.api;

import com.socialcircle.config.security.SecurityUtil;
import com.socialcircle.dao.ConnectDao;
import com.socialcircle.entity.Connect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ConnectAPI {

    @Autowired
    private ConnectDao connectDao;

    @Transactional(rollbackOn = ApiException.class)
    public Connect add(Long connectedWithUserId, Integer score, String notes) throws ApiException {
        Long userA = SecurityUtil.getPrincipal().getUserId();
        Long userB = connectedWithUserId;
        Connect pendingSuggestion = connectDao.getByUsers(Math.min(userA, userB), Math.max(userA, userB), true);

        if (Objects.isNull(pendingSuggestion)) {
            Connect connect = new Connect();
            connect.setSourceUserId(Math.min(userA, userB));
            connect.setDestinationUserId(Math.max(userA, userB));
            connect.setConnectTime(ZonedDateTime.now());
            connect.setScore(score);
            connect.setNotes(notes);
            connectDao.persist(connect);
            return connect;
        } else {
            pendingSuggestion.setIsSuggestion(false);
            pendingSuggestion.setConnectTime(ZonedDateTime.now());
            pendingSuggestion.setScore(score);
            pendingSuggestion.setNotes(notes);
            return pendingSuggestion;
        }
    }

    public List<Connect> getConnectsToUser(Long userId) throws ApiException {
        Long thisUserId = SecurityUtil.getPrincipal().getUserId();

        List<Connect> connects = connectDao.selectMultiple("sourceUser", userId);
        connects.addAll(connectDao.selectMultiple("sourceUser", userId));
        return connects.stream().filter(i -> i.getSourceUserId().equals(thisUserId) ||
                i.getDestinationUserId().equals(thisUserId)).collect(Collectors.toList());
    }

    public List<Connect> getRecentConnects() throws ApiException {
        Long thisUserId = SecurityUtil.getPrincipal().getUserId();

        List<Connect> connects = connectDao.selectMultiple("sourceUser", thisUserId);
        return connects.stream().filter(i -> i.getSourceUserId().equals(thisUserId) ||
                i.getDestinationUserId().equals(thisUserId)).sorted(Comparator.comparing(Connect::connectTime).reversed())
                .limit(10).collect(Collectors.toList());
    }

}
