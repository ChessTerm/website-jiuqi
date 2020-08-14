package com.chessterm.website.jiuqi.service.oauth;

import com.chessterm.website.jiuqi.model.AssociatedPlatform;
import com.chessterm.website.jiuqi.model.Association;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.parser.QueryStringParser;
import com.chessterm.website.jiuqi.repository.AssociationRepository;
import com.chessterm.website.jiuqi.repository.UserRepository;
import org.kohsuke.github.GHEmail;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Authorize with GitHub.
 * <p>
 * References:
 * https://docs.github.com/cn/developers/apps/authorizing-oauth-apps#web-application-flow
 */
@Service
public class GitHubOAuthProvider extends OAuthProvider {

    private static final AssociatedPlatform platform = AssociatedPlatform.GITHUB;

    private static final String authorizeUrl = "https://github.com/login/oauth/authorize";
    private static final String tokenUrl = "https://github.com/login/oauth/access_token";

    @Value("${spring.security.oauth2.client.registration.github.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.clientSecret}")
    private String clientSecret;

    @Autowired
    AssociationRepository repository;

    @Autowired
    UserRepository userRepository;

    /**
     * Step 1:
     * Redirect to this URL to authorize.
     *
     * @param scope <p>scopes to request</p>
     *              <p>All available scopes: https://docs.github.com/en/developers/apps/scopes-for-oauth-apps#available-scopes</p>
     * @return URL to redirect
     */
    public String getAuthorizeUrl(String scope) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(authorizeUrl);
        return builder
            .queryParam("client_id", clientId)
            .queryParam("scope", scope)
            .queryParam("allow_signup", "false")
            .toUriString();
    }

    /**
     * Step 2:
     * Use the code provided to request an OAuth token.
     *
     * @param code code provided by GitHub
     * @return OAuth token requested from GitHub
     */
    public String requestToken(String code) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(tokenUrl);
        String url = urlBuilder
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("code", code).toUriString();
        WebClient client = WebClient.create();
        String result = client.get().uri(url).retrieve().bodyToMono(String.class).block();
        if (result != null) {
            Map<String, String> resultMap = QueryStringParser.parse(result);
            return resultMap.getOrDefault("access_token", "");
        } else throw new IllegalArgumentException();
    }

    /**
     * Step 3:
     * <p>Save the OAuth token provided by GitHub and check if
     * the GitHub account has already associated with a user.</p>
     * <p>If associated, login directly;</p>
     * <p>If not, redirect to ask associate or create new user.</p>
     *
     * @param token OAuth token requested from GitHub
     * @return Whether to redirect
     */
    public boolean saveToken(String token, User user, boolean create) throws IOException {
        GitHub client = new GitHubBuilder().withOAuthToken(token).build();
        GHMyself githubUser = client.getMyself();
        long githubUserId = githubUser.getId();
        Association association = repository.findByPlatformAndAssociatedId(platform, githubUserId);
        Authentication authentication;
        if (association == null) {
            if (user == null) {
                if (create) {
                    user = createUser(githubUser);
                } else return false;
            }
            String email = getEmail(githubUser);
            user.setEmail(email);
            user.setName(githubUser.getName());
            association = new Association(platform, user);
            association.setAssociatedId(githubUserId);
            association.setToken(token);
            repository.save(association);
            user = userRepository.save(user);
            authentication = new PreAuthenticatedAuthenticationToken
                (user, token, user.getAuthorities());
        } else {
            String email = getEmail(githubUser);
            User associationUser = association.getUser();
            associationUser.setEmail(email);
            associationUser.setName(githubUser.getName());
            associationUser = userRepository.save(associationUser);
            authentication = new PreAuthenticatedAuthenticationToken
                (associationUser, token, associationUser.getAuthorities());
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    /**
     * Create a new user by the GitHub user.
     *
     * @param githubUser GitHub user object
     * @return The new user created
     */
    private User createUser(GHMyself githubUser) {
        User user = new User();
        User lastUser = userRepository.findByIdGreaterThanEqualOrderByIdDesc(10170200);
        long nextId = lastUser == null ? 10170200 : lastUser.getId() + 1;
        user.setId(nextId);
        user.setAdmin(false);
        user = userRepository.save(user);
        return user;
    }

    /**
     * <p>Get primary email address of a user.</p>
     * <p>Returns null when all email address is unverified.</p>
     *
     * @param user GitHub user myself object
     * @return Primary email address of this user
     */
    private String getEmail(GHMyself user) throws IOException {
        List<GHEmail> list = user.getEmails2();
        for (GHEmail email: list) {
            if (email.isVerified() && email.isPrimary()) return email.getEmail();
        }
        return null;
    }
}
