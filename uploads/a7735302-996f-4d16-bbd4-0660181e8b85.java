package com.example;

public class Book {
    private int number;
    private String callNumber;
    private String title;
    private String author;
    private int issueDate;
    private String issuer;

    public Book(int number, String callNumber, String title, String author, int issueDate, String issuer) {
        this.number = number;
        this.callNumber = callNumber;
        this.title = title;
        this.author = author;
        this.issueDate = issueDate;
        this.issuer = issuer;
    }

    public int getNumber() {
        return number;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getIssueDate() {
        return issueDate;
    }

    public String getIssuer() {
        return issuer;
    }
}
