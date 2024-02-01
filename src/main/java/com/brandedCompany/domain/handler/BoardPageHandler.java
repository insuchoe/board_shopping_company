package com.brandedCompany.domain.handler;

import com.brandedCompany.domain.searchCondition.BoardSearchCondition;
import com.brandedCompany.domain.searchCondition.BoardSearchCondition.Option;
import com.brandedCompany.domain.searchCondition.SearchCondition;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;

public class BoardPageHandler extends PageHandler {


    public BoardPageHandler(BigInteger TOTAL_COUNT, SearchCondition searchCondition) {
        super(10, TOTAL_COUNT, searchCondition);
    }

    public BoardSearchCondition getSearchCondition() {
        return (BoardSearchCondition) this.searchCondition;
    }

    @Override
    public String getQueryString() {
        return getSearchCondition().getQueryString();
    }

    @Override
    public String getQueryString(Integer page) {
        return getSearchCondition().getQueryString(page);

    }

    public String getKeyword() {
        return getSearchCondition().getKeyword();
    }

    public Option getOption() {
        return getSearchCondition().getOption();
    }

    @Override
    public String toString() {
        return "BoardPageHandler{" +
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
