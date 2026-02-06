package com.restuarant.app.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disable for API development
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/api/customer/slots", "/api/customer/search").permitAll()
                    .requestMatchers("/api/staff/**").hasRole("STAFF")
                    .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
	    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	    // Keycloak puts roles in "realm_access", but Spring expects them as authorities
	    // If you use a library like 'spring-boot-starter-oauth2-resource-server', 
	    // you might need a custom mapper to extract from realm_access.roles
	    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); 
	    
	    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
	    converter.setJwtGrantedAuthoritiesConverter(new Converter<Jwt, Collection<GrantedAuthority>>() {

	        @Override
	        public Collection<GrantedAuthority> convert(Jwt jwt) {

	            Map<String, Object> realmAccess =
	                    (Map<String, Object>) jwt.getClaims().get("realm_access");

	            if (realmAccess == null || realmAccess.get("roles") == null) {
	                return java.util.Collections.emptyList();
	            }

	            Collection<String> roles =
	                    (Collection<String>) realmAccess.get("roles");

	            return roles.stream()
	                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
	                    .collect(Collectors.toList());
	        }
	    });
	    return converter;
	}
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 1. Allow your React Frontend URL
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        
        // 2. Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 3. Allow Keycloak headers (Authorization is critical!)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        
        // 4. Allow credentials (cookies/auth headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
