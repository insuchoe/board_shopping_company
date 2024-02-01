package com.brandedCompany.exception;



import com.brandedCompany.util.DomainUtils.TABLE;

public class TableNotFoundException extends RuntimeException {
    public TableNotFoundException(TABLE table) {
        super(table.name() +"not found");
    }
}
