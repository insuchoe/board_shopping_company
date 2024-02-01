package com.brandedCompany.repository;

import com.brandedCompany.domain.*;
import com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition;
import com.brandedCompany.domain.searchCondition.SearchCondition;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.exception.TableNotFoundException;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.brandedCompany.util.DomainUtils.TABLE.*;
import static com.brandedCompany.util.DomainUtils.*;
import static java.util.stream.Collectors.toList;

@Repository
public class OptionRepositoryImpl implements OptionRepository {

    private final SqlSession session;
    private final PagingAndSortingRepository repository;

    @Autowired
    public OptionRepositoryImpl(SqlSession session, @Qualifier("pagingAndSortingRepositoryImpl") PagingAndSortingRepository repository) {
        this.session = session;
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
    public boolean isExistComment(BigInteger boardId) throws Exception {
        return repository.isExistComment(boardId);
    }

    @Override
    public void increaseViews(BigInteger boardId) throws Exception {
        repository.increaseViews(boardId);
    }

    private  final String MAPPER = "optionRepositoryMapper.";

    @Override
    public Collection<? extends Domain> selectByName(TABLE table, String name, NameLocation location) throws Exception {
        if (!(table == CUSTOMERS || table == EMPLOYEES || table == COMMENTS || table == BOARDS))
            throw new TableNotFoundException(table);
        Map map = mapping(getType(table), getMap());
        map.put("location", location);
        map.put("name", name);
        List<Object> unpacks = session.selectList(MAPPER + "selectByName", map);
//        System.out.println("unpacks = " + unpacks);
        List<? extends Domain> result = unpacks.stream().map(rows -> {
            try {
                return packDomain(rows);
            } catch (Exception e) {
                //      e.printStackTrace();
            }
            return null;
        }).collect(
                toList());
//        System.out.println("result = " + result);
        return result;

    }

    @Override
    public Collection<Board> selectBoardByEmpId(BigInteger employeeId) throws Exception {
        return session.selectList(MAPPER + "selectBoardByEmpId", employeeId);
    }

    @Override
    public BigInteger selectNextSequence(TABLE table) throws Exception {
        return session.selectOne(MAPPER+"selectNextSequence",mapping(getType(table),getMap()));
    }

    @Override
    public BigInteger selectNextBoardId() throws Exception
    {
        return session.selectOne(MAPPER + "selectNextBoardId");
    }

    @Override
    public BigInteger selectLastBoardId() throws Exception
    {
        return session.selectOne(MAPPER + "selectLastBoardId");
    }

    @Override
    public BigInteger selectLastCommentId() throws Exception
    {
        return session.selectOne(MAPPER + "selectLastCommentId");
    }


}
