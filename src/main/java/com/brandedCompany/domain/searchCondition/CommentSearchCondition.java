package com.brandedCompany.domain.searchCondition;

import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

@Getter
public class CommentSearchCondition extends SearchCondition {
    private BigInteger boardId, employeeId;

    public CommentSearchCondition(BigInteger boardId) {
        super(6, 3, 16, 6, 1);
        this.boardId = boardId;
    }

    public CommentSearchCondition( Integer page, BigInteger boardId) {
        super(6, 3, 16, 6, page);
        this.boardId = boardId;
    }

    public CommentSearchCondition(BigInteger boardId, BigInteger employeeId) {
        super(6, 3, 16, 6, 1);
        this.boardId = boardId;
        this.employeeId = employeeId;

    }

    public CommentSearchCondition(CommentSearchConditionBuilder builder) {
        super(6, 3, 16, 6, builder.getPAGE());
        this.boardId = builder.boardId;
        this.employeeId = builder.employeeId;
    }

    public CommentSearchCondition getSelf() {
        return this;
    }


   public static class CommentSearchConditionBuilder extends SearchCondition
   {
       private BigInteger boardId, employeeId;

       public CommentSearchConditionBuilder() {
           super(6, 3, 16, 6, 1);
       }

       public CommentSearchConditionBuilder page(Integer page)
       {
           this.PAGE=page;
           return this;
       }
       public CommentSearchConditionBuilder boardId(BigInteger boardId) {
           this.boardId = boardId;
           return this;
       }

       public CommentSearchConditionBuilder employeeId(BigInteger employeeId) {
           this.employeeId = employeeId;
           return this;
       }

       public CommentSearchCondition build()
       {
           return new CommentSearchCondition(this);
       }

       @Override
       public String getQueryString() {
           UriComponentsBuilder queryString= UriComponentsBuilder.newInstance();
           if(0!=PAGE)
               queryString.queryParam("commentPage",PAGE);
//           if(Optional.ofNullable(boardId).isPresent())
//               queryString.queryParam("boardId",boardId);
//           if(Optional.ofNullable(employeeId).isPresent())
//               queryString.queryParam("employeeId",employeeId);
           return queryString.build().toString();
       }

       @Override
       public String getQueryString(Integer page) {
           UriComponentsBuilder queryString= UriComponentsBuilder.newInstance();
           if(0!=page)
               queryString.queryParam("commentPage",page);
//           if(Optional.ofNullable(boardId).isPresent())
//               queryString.queryParam("boardId",boardId);
//           if(Optional.ofNullable(employeeId).isPresent())
//               queryString.queryParam("employeeId",employeeId);
           return queryString.build().toString();
       }
   }
    @Override
    public String getQueryString() {
        UriComponentsBuilder queryString= UriComponentsBuilder.newInstance();
        if(0!=PAGE)
            queryString.queryParam("commentPage",PAGE);
//        if(Optional.ofNullable(boardId).isPresent())
//            queryString.queryParam("boardId",boardId);
//        if(Optional.ofNullable(employeeId).isPresent())
//            queryString.queryParam("employeeId",employeeId);
        return queryString.build().toString();
    }

    @Override
    public String getQueryString(Integer page) {
        UriComponentsBuilder queryString= UriComponentsBuilder.newInstance();
        if(0!=page)
            queryString.queryParam("commentPage",page);
//        if(Optional.ofNullable(boardId).isPresent())
//            queryString.queryParam("boardId",boardId);
//        if(Optional.ofNullable(employeeId).isPresent())
//            queryString.queryParam("employeeId",employeeId);
        return queryString.build().toString();
    }

    public int getOffset() {
        return (PAGE - 1) * PAGE_SIZE;
    }



    public String getClassSimpleName() {
        return this.getClass().getSimpleName();
    }

}
