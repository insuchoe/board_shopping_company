package com.brandedCompany.domain.handler;

import com.brandedCompany.domain.searchCondition.SearchCondition;

import java.math.BigInteger;


public abstract class PageHandler {
    protected final SearchCondition searchCondition;
    protected final Integer NAV_SIZE;
    protected final BigInteger TOTAL_COUNT;
    protected final Integer TOTAL_PAGE, BEGIN_PAGE, END_PAGE;
    protected boolean showPrev = false;
    protected boolean showNext = false;


    public PageHandler(Integer NAV_SIZE, BigInteger TOTAL_COUNT, SearchCondition searchCondition) {
        this.NAV_SIZE = NAV_SIZE;
        this.TOTAL_COUNT = TOTAL_COUNT;

        this.TOTAL_PAGE = TOTAL_COUNT.intValue() / searchCondition.getPAGE_SIZE() + (TOTAL_COUNT.intValue() % searchCondition.getPAGE_SIZE() == 0 ? 0 : 1);
        this.searchCondition = searchCondition;
        //        page 가 totalPage 보다 크지 않게
        this.searchCondition.setPAGE(Math.min(searchCondition.getPAGE(),TOTAL_PAGE));
//        System.out.println("this.searchCondition.getPAGE() in handler= " + this.searchCondition.getPAGE());
        this.BEGIN_PAGE = (this.searchCondition.getPAGE() - 1) / NAV_SIZE * NAV_SIZE + 1; // 11 -> 11, 10 -> 1, 15->11. 따로 떼어내서 테스트
        this.END_PAGE = Math.min(BEGIN_PAGE + NAV_SIZE - 1, TOTAL_PAGE);
        this.showPrev = BEGIN_PAGE != 1;
        this.showNext = !END_PAGE.equals(TOTAL_PAGE);
    }

    public abstract String getQueryString();
    public String getQueryString2()
    {
        return  getQueryString().replace("?","&");
    }
    public Integer getPAGE() {
        return searchCondition.getPAGE();
    }

    public BigInteger getTOTAL_COUNT() {
        return TOTAL_COUNT;
    }
    public Integer getPAGE_SIZE()
    {
        return searchCondition.getPAGE_SIZE();
    }

    public Integer getTOTAL_PAGE() {
        return TOTAL_PAGE;
    }

    public boolean getShowPrev() {
        return showPrev;
    }

    public boolean getShowNext() {
        return showNext;
    }

    public Integer getBeginPage() {
        return BEGIN_PAGE;
    }

    public Integer getEndPage() {
        return END_PAGE;
    }

    public abstract String getQueryString(Integer PAGE);

    public String print() {
        StringBuilder builder = new StringBuilder();
        if (showPrev) builder.append("PREV ");
        for (int i = BEGIN_PAGE; i <= END_PAGE; i++) builder.append(i + " ");
        if (showNext) builder.append("NEXT");
        return builder.toString();
    }


}
