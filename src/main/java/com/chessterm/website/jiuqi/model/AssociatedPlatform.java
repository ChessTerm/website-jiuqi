package com.chessterm.website.jiuqi.model;

import com.chessterm.website.jiuqi.service.oauth.GitHubOAuthProvider;
import com.chessterm.website.jiuqi.service.oauth.OAuthProvider;
import lombok.Getter;

public enum AssociatedPlatform {

    GITHUB(GitHubOAuthProvider.class);

    @Getter
    private final Class<? extends OAuthProvider> provider;

    AssociatedPlatform(Class<? extends OAuthProvider> provider) {
        this.provider = provider;
    }
}
