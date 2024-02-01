package com.brandedCompany.serivce;

import com.brandedCompany.domain.*;
import com.brandedCompany.domain.searchCondition.SearchCondition;
import com.brandedCompany.repository.CRUDRepositoryImpl;
import com.brandedCompany.repository.OptionRepository;
import com.brandedCompany.util.DomainUtils;
import com.brandedCompany.util.DomainUtils.TABLE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;

@Service
public class OptionServiceImpl implements OptionService {
    @Autowired
    OptionRepository repository;

    static final Logger logger = LoggerFactory.getLogger(CRUDRepositoryImpl.class);

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


    public BigInteger selectNextSequence(TABLE table) throws Exception {
        return repository.selectNextSequence(table);
    }

    @Override
    public BigInteger selectNextBoardId() throws Exception
    {
        return repository.selectNextBoardId();
    }

    @Override
    public BigInteger selectLastBoardId() throws Exception
    {
        return repository.selectLastBoardId();
    }
    @Override
    public Collection<? extends Domain> selectByName(TABLE table, String name, DomainUtils.NameLocation location) throws Exception {
        return repository.selectByName(table, name, location);
    }
    @Override
    public Collection<? extends
            Domain> selectBoardByEmpId(BigInteger employeeId) throws Exception {
        return repository.selectBoardByEmpId(employeeId);
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
    public boolean isExistComment(BigInteger boardId) throws Exception {
        return repository.isExistComment(boardId);
    }

    @Override
    public void increaseViews(BigInteger boardId) throws Exception {
        repository.increaseViews(boardId);
    }

    @Override
    public BigInteger selectLastCommentId() throws Exception
    {
        return repository.selectLastCommentId();
    }






}
