package com.socialcircle.dao;

import com.socialcircle.entity.Connect;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class ConnectDao extends AbstractDao<Connect> {

    public Connect getPendingByUsers(Long userId, Long otherUserId) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Connect> query = cb.createQuery(Connect.class);
        Root<Connect> root = query.from(Connect.class);

        query.where(cb.and(
                        cb.equal(root.get("sourceUserId"), Math.min(userId, otherUserId))),
                        cb.equal(root.get("destinationUserId"), Math.max(userId, otherUserId)),
                        cb.equal(root.get("isSuggestion"), true)
                );
        TypedQuery<Connect> tQuery = createQuery(query);

        return selectSingleOrNull(tQuery);
    }

    public List<Connect> getByUsers(Long userId, Long otherUserId) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Connect> query = cb.createQuery(Connect.class);
        Root<Connect> root = query.from(Connect.class);

        query.where(cb.and(
                        cb.equal(root.get("sourceUserId"), Math.min(userId, otherUserId))),
                cb.equal(root.get("destinationUserId"), Math.max(userId, otherUserId)),
                cb.equal(root.get("isSuggestion"), false)
        );
        TypedQuery<Connect> tQuery = createQuery(query);

        return selectMultiple(tQuery);
    }

}
