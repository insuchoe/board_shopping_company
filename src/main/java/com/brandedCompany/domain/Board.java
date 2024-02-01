package com.brandedCompany.domain;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board extends Domain {
    private BigInteger boardId, employeeId, views;
    private String title, content, publisher;
    private LocalDateTime publishedDate;

    public Board(BigInteger boardId, BigInteger employeeId, String content,String title) {
        this.boardId = boardId;
        this.employeeId = employeeId;
        this.content = content;
        this.title=title;
    }

    public Board(BigInteger boardId, BigInteger employeeId) {
        this.boardId = boardId;
        this.employeeId = employeeId;
    }

    public Board(@NotNull BigInteger employeeId, String publisher, String title, String content) {

        this.title = title;
        this.content = content;
        this.publishedDate = LocalDateTime.now();
        this.views = BigInteger.ZERO;
        this.publisher = publisher;
        this.employeeId = employeeId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return boardId.equals(board.boardId) && employeeId.equals(board.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, employeeId);
    }
}
