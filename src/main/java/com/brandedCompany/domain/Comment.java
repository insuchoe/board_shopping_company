package com.brandedCompany.domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@ToString
@NoArgsConstructor
public class Comment extends Domain{
    private BigInteger commentId,boardId,employeeId, parentCommentId;
    private String  content,publisher;
    private String  imageBase64;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime registrationDate,modificationDate;

    public Comment( BigInteger boardId,BigInteger employeeId, String content, String publisher) {
       this.boardId=boardId;
        this.employeeId = employeeId;
        this.content = content;
        this.publisher = publisher;
        this.parentCommentId = commentId;
        this.registrationDate=LocalDateTime.now();
    }
    public Comment( BigInteger boardId,BigInteger parentCommentId,BigInteger employeeId, String content, String publisher) {
        this.boardId=boardId;
        this.employeeId = employeeId;
        this.content = content;
        this.publisher = publisher;
        this.parentCommentId = parentCommentId;
        this.registrationDate=LocalDateTime.now();
    }

    public Comment(BigInteger commentId, BigInteger boardId, BigInteger employeeId, BigInteger parentCommentId, String content, String publisher, LocalDateTime registrationDate, LocalDateTime modificationDate)
    {
        this.commentId = commentId;
        this.boardId = boardId;
        this.employeeId = employeeId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.publisher = publisher;
        this.registrationDate = registrationDate;
        this.modificationDate = modificationDate;
    }

    public Comment(BigInteger commentId, BigInteger parentCommentId) {
        this.commentId = commentId;
        this.parentCommentId = parentCommentId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(commentId, comment.commentId) && Objects.equals(parentCommentId, comment.parentCommentId) && Objects.equals(parentCommentId, comment.parentCommentId);
    }

    public void setImageBase64(String imageBase64)
    {
        this.imageBase64 = imageBase64;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, boardId, parentCommentId);
    }


}
