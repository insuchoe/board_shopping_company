package com.brandedCompany.repository;

import com.brandedCompany.domain.*;
import com.brandedCompany.domain.searchCondition.SearchCondition;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.util.DomainUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.brandedCompany.util.DomainUtils.TABLE;

@Repository
public class PagingAndSortingRepositoryImpl implements PagingAndSortingRepository {
    CRUDRepository crudRepository;
    SqlSession session;

    @Autowired
    public PagingAndSortingRepositoryImpl(@Qualifier("CRUDRepositoryImpl") CRUDRepository crudRepository, SqlSession session) {
        this.crudRepository = crudRepository;
        this.session = session;
    }
    private static final String MAPPER = "pagingAndSortingRepositoryMapper.";
    @Override
    public BigInteger count(TABLE table) throws Exception {
        return crudRepository.count(table);

    }


    @Override
    public void delete(TABLE table) throws Exception {
        crudRepository.delete(table);
    }


    @Override
    public boolean delete(TABLE table, BigInteger... ids) throws Exception {
        return crudRepository.delete(table, ids);

    }

    @Override
    public boolean isExist(TABLE table, BigInteger... ids) throws Exception {
        return crudRepository.isExist(table, ids);

    }

    @Override
    public Collection<? extends Domain> select(TABLE table) throws Exception {
        return crudRepository.select(table);
    }

    @Override
    public Collection<? extends Domain> selectAll(TABLE table, BigInteger... ids) throws Exception {
        return crudRepository.selectAll(table,ids);
    }


    @Override
    public Domain select(TABLE table, BigInteger... ids) throws Exception {
        return crudRepository.select(table, ids);
    }


    @Override
    public boolean update(Domain domain) throws Exception {
        return crudRepository.update(domain);
    }


    @Override
    public boolean insert(Domain domain) throws Exception {
        return crudRepository.insert(domain);
    }






    @Override
    public BigInteger count(SearchCondition condition) throws ClassNotFoundException {
        return session.selectOne(MAPPER+"count", condition);
    }

    @Override
    public Collection<? extends Domain> pageAndSort(SearchCondition condition) throws Exception {
        List<Domain> result = new ArrayList<>();
        for (Object o : session.selectList(MAPPER + "pageAndSort", condition)) result.add(DomainUtils.packDomain(o));
        return result;
    }

    @Override
    public Board selectRecentBoard() throws Exception {
        return session.selectOne(MAPPER+"selectRecentBoard");
    }

    @Override
    public boolean isExistComment(BigInteger boardId) throws Exception
    {
        return session.selectOne(MAPPER+"isExistComment",boardId);
    }

    @Override
    public void increaseViews(BigInteger boardId) throws Exception {
        session.insert(MAPPER+"increaseViews",boardId);
    }


}
