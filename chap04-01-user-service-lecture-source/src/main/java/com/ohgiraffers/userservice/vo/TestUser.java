package com.ohgiraffers.userservice.vo;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/* memo: 불변객체여서 @ DATA가 안됨? */

public class TestUser extends User {

    private String username;
    private String item1;
    private String item2;
    private String item3;

    public TestUser(String username,
                    String password,
                    boolean enabled,
                    boolean accountNonExpired,
                    boolean credentialsNonExpired,
                    boolean accountNonLocked,
                    Collection<? extends GrantedAuthority> authorities,
                    String username1,
                    String item1,
                    String item2,
                    String item3) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.username = username1;
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }
}
