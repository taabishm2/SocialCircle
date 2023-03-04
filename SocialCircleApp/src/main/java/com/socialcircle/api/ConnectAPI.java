package com.socialcircle.api;

import com.socialcircle.config.security.SecurityUtil;
import com.socialcircle.dao.ConnectDao;
import com.socialcircle.entity.Connect;
import com.socialcircle.model.form.ConnectForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConnectAPI {

    @Autowired
    private ConnectDao connectDao;

    public Connect add(ConnectForm form) throws ApiException {
        Connect connect = new Connect();
        connect.setSourceUserId(SecurityUtil.getPrincipal().getUserId());
        connect.setDestinationUserId(form.getConnectedWithUserId());
        connect.setConnectTime(form.getConnectTime());
        connect.setNotes(form.getNotes());
        connectDao.persist(connect);

        return connect;
    }

    public List<Connect> getConnectsToUser(Long userId) throws ApiException {
        Long thisUserId = SecurityUtil.getPrincipal().getUserId();

        List<Connect> connects = connectDao.selectMultiple("sourceUser", userId);
        connects.addAll(connectDao.selectMultiple("sourceUser", userId));
        return connects.stream().filter(i -> i.getSourceUserId().equals(thisUserId) ||
                i.getDestinationUserId().equals(thisUserId)).collect(Collectors.toList());
    }

}
