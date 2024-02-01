package com.brandedCompany.domain.searchCondition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public abstract class SearchCondition {
    protected  final Integer DEFAULT_PAGE_SIZE,MIN_PAGE_SIZE,MAX_PAGE_SIZE,PAGE_SIZE ;
    protected Integer PAGE;

    public int getOffset()
    {
        return (PAGE-1)*PAGE_SIZE;
    }

    public abstract String getQueryString();

    public abstract String getQueryString(Integer PAGE);

    public void setPAGE(Integer PAGE) {
        this.PAGE = PAGE;
    }
}
