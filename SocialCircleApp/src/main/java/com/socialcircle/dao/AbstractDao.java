package com.socialcircle.dao;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractDao<T> {

    @PersistenceContext
    protected EntityManager em;

    private static final int ZERO_INT = 0;
    private static final int PAGE_DEFAULT_SIZE = 100;

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected EntityManager em() {
        return em;
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Transactional
    protected TypedQuery<T> createQuery(CriteriaQuery<T> cq) {
        return em.createQuery(cq);
    }

    @Transactional(readOnly = true)
    protected TypedQuery<T> getSelectQuery() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> r = cq.from(clazz);
        cq.select(r);
        return createQuery(cq);
    }

    @Transactional
    public TypedQuery<T> createJpqlQuery(String q) {
        return em.createQuery(q, clazz);
    }

    @Transactional
    public Query createQuery(String q) {
        return em.createQuery(q);
    }

    @Transactional
    public Query createSqlQuery(String q) {
        return em.createNativeQuery(q);
    }

    public Query createSqlQueryForEntity(String q) {
        return em.createNativeQuery(q, clazz);
    }

    @Transactional(readOnly = true)
    public Serializable getIdentifier(T obj) {
        return (Serializable) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(obj);
    }

    // STATE MANAGEMENT METHODS
    @Transactional(propagation = Propagation.MANDATORY)
    public void detach(T entity) {
        em.detach(entity);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void clear() {
        em.clear();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void flush() {
        em.flush();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void flushAndClear() {
        flush();
        clear();
    }

    // BASIC QUERIES
    @Transactional
    public void persist(T entity) {
        em.persist(entity);
    }

    @Transactional
    public void remove(T entity) {
        em.remove(entity);
    }

    @Transactional
    public void update(T entity) {
        // TODO nothing
    }

    // DELETE QUERIES
    @Transactional
    public void remove(Serializable id) {
        T entity = select(id);
        if (entity == null) {
            return;
        }
        remove(entity);
    }

    // SELECT QUERIES
    @Transactional(readOnly = true)
    public T select(Serializable id) {
        return em.find(clazz, id);
    }

    @Transactional(readOnly = true)
    private TypedQuery<T> getEntitySelectCriteriaQuery() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> from = cq.from(clazz);
        cq.select(from);
        TypedQuery<T> tq = em.createQuery(cq);
        return tq;
    }

    @Transactional(readOnly = true)
    public T select(String member, Object value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        ParameterExpression<Object> p = cb.parameter(Object.class);
        Root<T> from = cq.from(clazz);
        cq.select(from).where(cb.equal(from.get(member), p));
        TypedQuery<T> query = em.createQuery(cq);
        query.setParameter(p, value);
        return selectSingleOrNull(query);
    }

    // SELECT ALL QUERIES
    @Transactional(readOnly = true)
    public List<T> selectAll() {
        TypedQuery<T> tq = getEntitySelectCriteriaQuery();
        return selectMultiple(tq);
    }

    @Transactional(readOnly = true)
    public List<T> selectAll(int limit) {
        TypedQuery<T> tq = getEntitySelectCriteriaQuery();
        return selectMultiple(tq, limit);
    }

    // COUNT QUERIES
    @Transactional(readOnly = true)
    public long countAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> r = cq.from(clazz);
        cq.select(cb.count(r));
        return count(cq);
    }

    @Transactional(readOnly = true)
    public long count(CriteriaQuery<Long> cq) {
        return em.createQuery(cq).getSingleResult();
    }

    // UTILITY METHODS
    public static <T> T selectSingleOrNull(TypedQuery<T> query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> selectMultiple(TypedQuery<T> query, int limit) {
        return selectMultiple(query, 1, limit);
    }

    public static <T> List<T> selectMultiple(TypedQuery<T> query) {
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<T>();
        }
    }

    @Deprecated
    public List<T> selectMultipleTopK(TypedQuery<T> query) {
        return selectMultiple(query, 5000);
    }


    public List<T> selectMultiple(TypedQuery<T> query, int pageNo, int pageSize) {
        try {
            query.setFirstResult((pageNo - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<T>();
        }
    }

    @Transactional(readOnly = true)
    public List<T> selectMultiple(String member, Object value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        ParameterExpression<Object> p = cb.parameter(Object.class);
        Root<T> from = cq.from(clazz);
        cq.select(from).where(cb.equal(from.get(member), p));
        TypedQuery<T> query = em.createQuery(cq);
        query.setParameter(p, value);
        return selectMultiple(query);
    }

    private int getPageIndex(Integer pageNo) {
        if (pageNo != null && pageNo > 0)
            return pageNo - 1;
        return 0;
    }

}