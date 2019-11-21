package oauth2.authorizationserver.simple.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.KeyPair;

@Profile("simple")
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private KeyPair keyPair;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 注入authenticationManager来支持 password grant type
     */
    @Autowired
    private AuthenticationManager authenticationManager;
    /**
     * 配置授权服务器的安全，意味着实际上是/oauth/token端点,/oauth/authorize端点也应该是安全的
     * 默认的设置覆盖到了绝大多数需求，所以一般情况下你不需要做任何事情。
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
        //支持post形式的client认证
        security.allowFormAuthenticationForClients();
        // 默认tokenKeyAccess和checkTokenAccess对应端口权限是denyAll(),如果允许资源服务器调用这些端口，则需要覆盖默认配置
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
        // 解决OPTION /oauth/token 请求跨域问题
        security.addTokenEndpointAuthenticationFilter(new CorsFilter(corsConfigurationSource()));
    }

    public AuthorizationServerConfig(KeyPair keyPair) throws Exception {
        this.keyPair = keyPair;
    }

    /**
     * 此处通过配置ClientDetailsService，来配置注册到此授权服务器的客户端Clients信息。
     * 注意，除非在下面的configure(AuthorizationServerEndpointsConfigurer)中指定了
     * 一个AuthenticationManager，否则密码授权方式不可用。
     * 至少要配置一个client，否则服务器将不会启动。
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // client_id
                .withClient("client-for-server")
                // client_secret
                .secret(passwordEncoder.encode("client-for-server"))
                // 此Client支持的授权类型。OAuth2的Client请求code时会传递授权类型参数，此处包含的授权类型才可以访问
                .authorizedGrantTypes("authorization_code", "implicit","password")
                // 此Client分配的access_token的有效时间，要小于刷新时间
                .accessTokenValiditySeconds(7200)
                // 此Client分配的access_token的可刷新时间，要大于有效时间。超过有效时间，但是在可刷新时间范围的access_token可以刷新
                .refreshTokenValiditySeconds(72000)
                // 重定向URL
                .redirectUris("http://localhost:8080/login/oauth2/code/custom")
                .additionalInformation()
                // 此Client拥有的权限，资源服务器可以依据此处定义的权限对Client进行鉴权。
                .authorities("ROLE_CLIENT")
                // 此Client可以访问的资源的范围，资源服务器可以依据此处定义的范围对Client进行鉴权。
                .scopes("profile", "email", "phone")
                // 自动批准的范围（scope），自动批准的scope在批准页不需要显示，即不需要用户确认批准，如果所有scope都自动批准，
                // 则不显示批准页
                .autoApprove("profile");
    }

    /**
     * 该方法是用来配置授权服务器端点特性（Authorization Server endpoints），主要是一些非安全的特性。
     * 比如token存储、token自定义、授权类型等等的
     * 默认不需要任何配置如果是需要密码授权则需要提供一个AuthenticationManager
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(jwtAccessTokenConverter());
        endpoints.tokenStore(jwtTokenStore());
        //注入authenticationManager来支持 password grant type
        endpoints.authenticationManager(authenticationManager);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 1
        corsConfiguration.addAllowedHeader("*"); // 2
        corsConfiguration.addAllowedMethod("*"); // 允许所有方法包括"GET", "POST", "DELETE", "PUT"等等
        corsConfiguration.setMaxAge(1800l);//30分钟
        //使用setAllowCredentials的方式解决跨域问题只支持ie10以上。
        //如果使用默认的配合CookierHttpSessionStrategy的session方式
        // 前后端一起开启，开启之后就能够读写浏览器的Cookies。该字段可选。它的值是一个布尔值，表示是否允许发送Cookie。
        // 默认情况下，Cookie不包括在CORS请求之中。设为true，即表示服务器明确许可，
        // Cookie可以包含在请求中，一起发给服务器。这个值也只能设为true，如果服务器不要浏览器发送Cookie，删除该字段即可。
        corsConfiguration.setAllowCredentials(true);
        //配合HeaderHttpSessionStrategy的session方式。token的方式解决跨域问题。
        // CORS请求时。XMLHttpRequest对象的getResponseHeader()方法只能拿到6个基本字段：Cache-Control、Content-Language、Content-Type、Expires、Last-Modified、Pragma。如果想拿到其他字段，
        // 就必须在Access-Control-Expose-Headers里面指定。上面的例子指定，getResponseHeader(‘FooBar’)可以返回FooBar字段的值。
        //允许clienHeaderWriterFiltert-site取得自定义得header值
        corsConfiguration.addExposedHeader(HttpHeaders.AUTHORIZATION);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(jwtTokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Bean
    public CustomJwtAccessTokenConverter jwtAccessTokenConverter() {
        CustomJwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
        converter.setKeyPair(this.keyPair);
        return converter;
    }

    /**
     * jdbc token 配置
     */
    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

}
