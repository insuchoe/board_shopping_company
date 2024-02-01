package com.brandedCompany.serivce;

import com.brandedCompany.domain.Domain;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.repository.CRUDRepositoryImpl;
import com.brandedCompany.util.DomainUtils.TABLE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;

@Service
public class CRUDServiceImpl implements CRUDService {

    CRUDRepositoryImpl crudRepository;

    @Autowired
    public void setCrudRepository( @Qualifier("CRUDRepositoryImpl") CRUDRepositoryImpl crudRepository)
    {
        this.crudRepository = crudRepository;
    }


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
    public Collection<? extends Domain> selectAll(TABLE table,BigInteger... ids) throws Exception {
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





}
