package oauth2.authorizationserver.simple.config;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * 2.3.5版本JWT token没有sub字段，不符合标准，自定义补充sub字段
 */
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        result.getAdditionalInformation().put("sub", authentication.getName());
        return super.enhance(result, authentication);
    }
}
