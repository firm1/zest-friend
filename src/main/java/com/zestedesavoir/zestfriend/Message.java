package com.zestedesavoir.zestfriend;

import java.util.List;

public class Message {
    private int id;
    private Member author;
    private List<Member> participants;
    private String title;
    private String subtitle;


    public Message(int id, Member author, List<Member> participants, String title, String subtitle) {
        this.id = id;
        this.author = author;
        this.participants = participants;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Member> getParticipants() {
        return participants;
    }

    public Member getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Message{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                '}';
    }
}
