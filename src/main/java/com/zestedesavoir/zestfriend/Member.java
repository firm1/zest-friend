package com.zestedesavoir.zestfriend;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private int id;
    private String username;
    private String avatarUrl;
    private boolean isActive;
    private String lastVisitDate;
    private String joinedDate;
    public static List<Member> members = new ArrayList<>();

    public Member(int id, String username, String avatarUrl, boolean isActive, String lastVisitDate, String joinedDate) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.isActive = isActive;
        this.lastVisitDate = lastVisitDate;
        this.joinedDate = joinedDate;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Member)) return false;
        return id == ((Member) obj).getId();
    }

    @Override
    public String toString() {
        return "Member{" +
                "username='" + username + '\'' +
                '}';
    }
}
