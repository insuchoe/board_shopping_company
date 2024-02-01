package com.brandedCompany.domain.searchCondition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
public class BoardSearchCondition extends SearchCondition {
    private String keyword;
    private Option option;

    public enum Option {
        TITLE_CONTENT,
        CONTENT,
        TITLE,
        PUBLISHER,
        PUBLISHER_CONTENT;

        @Override
        public String toString() {
            return this.name();
        }
    }

    public BoardSearchCondition(){
        super(10, 5, 50, 10,
                1);
        this.keyword = "";
        this.option = null;
    }
    public BoardSearchCondition(Integer page,String keyword, String option){
        super(10, 5, 50, 10,
                page);
        this.keyword = keyword;
        this.option = "".equals(option) ? null : Option.valueOf(option);
    }

    public BoardSearchCondition(String keyword, Option option)
    {
        super(10, 5, 50, 10,
                1);
       this.keyword = keyword;
        this.option = option;
    }
    public BoardSearchCondition(BoardSearchConditionBuilder builder) {
        super(10, 5, 50, 10,
                builder.getPAGE());

        this.keyword = builder.keyword;
        this.option = builder.option;
    }


    public static class BoardSearchConditionBuilder extends SearchCondition
    {

        private String keyword;
        private Option option;

        public BoardSearchConditionBuilder() {
            super(10, 5, 50, 10,1);
        }

        public BoardSearchConditionBuilder page(Integer page)
        {
            this.PAGE=page;
            return this;
        }
        public BoardSearchConditionBuilder keyword(String keyword)
        {
            this.keyword=keyword;
            return this;
        }
        public BoardSearchConditionBuilder option(Option option)
        {
            this.option=option;
            return this;
        }
        public BoardSearchCondition build()
        {
            return new BoardSearchCondition(this);
        }

        @Override
        public String getQueryString()
        {
            UriComponentsBuilder queryString=UriComponentsBuilder.newInstance();
            if(0!=PAGE)
                queryString.queryParam("page",PAGE);
                if(Optional.ofNullable(keyword).isPresent())
                    queryString.queryParam("keyword",keyword);
                if(Optional.ofNullable(option).isPresent())
                    queryString.queryParam("option",option.name());
            return queryString.build().toString();
        }
        @Override
        public String getQueryString(Integer page)
        {
            UriComponentsBuilder queryString=UriComponentsBuilder.newInstance();
            if(0!=page)
                queryString.queryParam("page",page);
            if(Optional.ofNullable(keyword).isPresent())
                queryString.queryParam("keyword",keyword);
            if(Optional.ofNullable(option).isPresent())
                queryString.queryParam("option",option.name());
            return queryString.build().toString();
        }

    }


    public BoardSearchCondition getSelf() {
        return this;
    }

    @Override
    public String getQueryString()
    {
        UriComponentsBuilder queryString=UriComponentsBuilder.newInstance();
        if(0!=PAGE)
        queryString.queryParam("page",PAGE);
        if(Optional.ofNullable(keyword).isPresent())
            queryString.queryParam("keyword",keyword);
        if(Optional.ofNullable(option).isPresent())
            queryString.queryParam("option",option.name());
        return queryString.build().toString();
    }
    @Override
    public String getQueryString(Integer page)
    {
        UriComponentsBuilder queryString=UriComponentsBuilder.newInstance();
        if(0!=page)
            queryString.queryParam("page",page);
        if(Optional.ofNullable(keyword).isPresent())
            queryString.queryParam("keyword",keyword);
        if(Optional.ofNullable(option).isPresent())
            queryString.queryParam("option",option.name());
        return queryString.build().toString();
    }


    public String getQueryString2() {
        return getQueryString().replace("?", "&");
    }

    public int getOffset() {
        return (PAGE - 1) * PAGE_SIZE;
    }

    public String getOptionName() {
        return option.name();
    }

    public String getClassSimpleName() {
        return this.getClass().getSimpleName();
    }




}
