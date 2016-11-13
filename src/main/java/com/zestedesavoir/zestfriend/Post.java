package com.zestedesavoir.zestfriend;


public class Post {
    int id;
    String text;
    Member author;

    public Post(int id, String text, Member author) {
        this.id = id;
        this.text = text;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public Member getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
