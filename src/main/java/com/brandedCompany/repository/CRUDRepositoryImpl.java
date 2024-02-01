package com.brandedCompany.repository;

import com.brandedCompany.domain.Domain;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.*;

import static com.brandedCompany.util.DomainUtils.*;
import static java.util.stream.Collectors.toList;

@Repository
public class CRUDRepositoryImpl implements CRUDRepository
{
    private static final String CRUD_REPOSITORY_MAPPER = "crudRepositoryMapper.";

    @Autowired SqlSession session;

    @Override
    public BigInteger count(TABLE table) throws Exception
    {
        //        Class<? extends Domain> type = getType(table);
        //        return CRUD.super.count(session, type);
        return session.selectOne(CRUD_REPOSITORY_MAPPER + "count", mapping(getType(table), getMap()));

    }


    @Override
    public void delete(TABLE table) throws Exception
    {
        //        CRUD.super.delete(session, getType(table));
        session.delete(CRUD_REPOSITORY_MAPPER + "delete", mapping(getType(table), getMap()));
    }


    @Override
    public boolean delete(TABLE table, BigInteger... ids) throws Exception
    {
        Map map = mapping(getType(table), getMap());
        int i = 0;
        for (BigInteger id : ids)
        {
            map.put("id" + (++i), id);
        }
        int resultInt = session.delete(CRUD_REPOSITORY_MAPPER + "delete", map);
        return resultInt == 1
            ? true
            : false;
    }

    @Override
    public boolean isExist(TABLE table, BigInteger... ids) throws Exception
    {
        Map map = mapping(getType(table), getMap());
        int i = 0;
        for (BigInteger id : ids)
        {
            map.put("id" + (++i), id);
        }
        int resultInt = session.selectOne(CRUD_REPOSITORY_MAPPER + "isExist", map);
        return resultInt == 1;
    }

    @Override
    public Collection<? extends Domain> select(TABLE table) throws Exception
    {
        return session.selectList(CRUD_REPOSITORY_MAPPER + "select", mapping(getType(table), getMap()))
                      .stream()
                      .map(rows ->
                           {
                               try
                               {
                                   return packDomain(rows);
                               }
                               catch (Exception e)
                               {
                                   //               e.printStackTrace();
                               }
                               return null;
                           })
                      .collect(toList());
    }

    @Override
    public Collection<? extends Domain> selectAll(TABLE table, BigInteger... ids) throws Exception
    {
        Class<? extends Domain> type = getType(table);
        Map map = mapping(type, getMap());
        int i = 0;
        for (BigInteger id : ids)
        {
            map.put("id" + (++i), id);
        }
        List<Object> objects = session.selectList(CRUD_REPOSITORY_MAPPER + "select", map);
        List<Domain> result = new ArrayList<>();
        for (Object object : objects)
        {
            result.add(packDomain(object));
        }
        return (Collection<? extends Domain>) result;
    }


    @Override
    public Domain select(TABLE table, BigInteger... ids) throws Exception
    {
        Class<? extends Domain> type = getType(table);
        Map map = mapping(type, getMap());
        int i = 0;
        for (BigInteger id : ids)
        {
            map.put("id" + (++i), id);
        }
        Object rows = session.selectOne(CRUD_REPOSITORY_MAPPER + "select", map);
        Domain result = packDomain(rows);
        return result;
    }


    @Override
    public boolean update(Domain domain) throws Exception
    {
        Map map = getMap();
        map.put("domain", domain);
        return session.update(CRUD_REPOSITORY_MAPPER + "update", map) == 1;
    }


    @Override
    public boolean insert(Domain domain) throws Exception
    {
        return session.insert(CRUD_REPOSITORY_MAPPER + "insert", mapping(domain, getMap())) == 1;
    }



}