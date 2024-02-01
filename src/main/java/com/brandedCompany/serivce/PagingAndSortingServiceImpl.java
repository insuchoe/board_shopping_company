package com.brandedCompany.serivce;

import com.brandedCompany.domain.*;
import com.brandedCompany.domain.searchCondition.SearchCondition;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.repository.PagingAndSortingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;

import static com.brandedCompany.util.DomainUtils.TABLE;

@Repository
public class PagingAndSortingServiceImpl implements PagingAndSortingService {

    private final PagingAndSortingRepository repository;

    @Autowired
    public PagingAndSortingServiceImpl(@Qualifier("pagingAndSortingRepositoryImpl") PagingAndSortingRepository repository) {
        this.repository = repository;
    }

    @Override
    public BigInteger count(TABLE table) throws Exception {
        return repository.count(table);
    }


    @Override
    public void delete(TABLE table) throws Exception {
        repository.delete(table);
    }


    @Override
    public boolean delete(TABLE table, BigInteger... ids) throws Exception {
        return repository.delete(table, ids);
    }

    @Override
    public boolean isExist(TABLE table, BigInteger... ids) throws Exception {
        return repository.isExist(table, ids);
    }

    @Override
    public Collection<? extends Domain> select(TABLE table) throws Exception {
        return repository.select(table);
    }

    @Override
    public Collection<? extends Domain> selectAll(TABLE table, BigInteger... ids) throws Exception {
        return repository.selectAll(table,ids);
    }


    @Override
    public Domain select(TABLE table, BigInteger... ids) throws Exception {
        return repository.select(table, ids);
    }


    @Override
    public boolean update(Domain domain) throws Exception {
        return repository.update(domain);
    }


    @Override
    public boolean insert(Domain domain) throws Exception {
        return repository.insert(domain);
    }


    @Override
    public BigInteger count(SearchCondition condition) throws ClassNotFoundException {
        return repository.count(condition);
    }

    @Override
    public Collection<? extends Domain> pageAndSort(SearchCondition condition) throws Exception {
        return repository.pageAndSort(condition);
    }

    @Override
    public Board selectRecentBoard() throws Exception {
        return repository.selectRecentBoard();
    }

    @Override
    public boolean isExistComment(BigInteger boardId) throws Exception
    {
        return repository.isExistComment(boardId);
    }

    @Override
    public void increaseViews(BigInteger boardId) throws Exception {
        repository.increaseViews(boardId);
    }
}
