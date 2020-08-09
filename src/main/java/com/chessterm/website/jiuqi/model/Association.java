package com.chessterm.website.jiuqi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Association {

    public Association() {}

    public Association(AssociatedPlatform platform, User user) {
        this.platform = platform;
        this.user = user;
    }

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private AssociatedPlatform platform;

    private long associatedId;

    @OneToOne
    private User user;

    @JsonIgnore
    private String token;
}
