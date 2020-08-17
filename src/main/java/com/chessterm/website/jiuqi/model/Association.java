package com.chessterm.website.jiuqi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Association {

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

    public Association(AssociatedPlatform platform, User user) {
        this.platform = platform;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Association that = (Association) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
