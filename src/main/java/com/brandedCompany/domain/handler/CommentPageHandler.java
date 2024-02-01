package com.brandedCompany.domain.handler;

import com.brandedCompany.domain.searchCondition.CommentSearchCondition;
import lombok.ToString;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;

public class CommentPageHandler extends PageHandler {
    public CommentPageHandler( BigInteger TOTAL_COUNT, CommentSearchCondition searchCondition) {
        super(5, TOTAL_COUNT, searchCondition);
    }

    @Override
    public String getQueryString() {
        return getSearchCondition().getQueryString();
    }


    @Override
    public String getQueryString(Integer page) {
        return getSearchCondition().getQueryString(page);
    }
    public String getQueryString2() {
        return getQueryString().replace("?","&");
    }
    public String getQueryString2(Integer page) {
        return getSearchCondition().getQueryString(page).replace("?","&");
    }

    public CommentSearchCondition getSearchCondition()
    {
        return (CommentSearchCondition)this.searchCondition;
    }

    @Override
    public String toString() {
        return "CommentPageHandler{" +
                "searchCondition=" + searchCondition +
                ", NAV_SIZE=" + NAV_SIZE +
                ", TOTAL_COUNT=" + TOTAL_COUNT +
                ", TOTAL_PAGE=" + TOTAL_PAGE +
                ", BEGIN_PAGE=" + BEGIN_PAGE +
                ", END_PAGE=" + END_PAGE +
                ", showPrev=" + showPrev +
                ", showNext=" + showNext +
                '}';
    }
}
