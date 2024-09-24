package com.obss.mentorapp.config;

import com.obss.mentorapp.security.GoogleTokenVerifier;
import com.obss.mentorapp.service.PasswordUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class AppConfig {

    @Bean
    public GoogleTokenVerifier googleTokenVerifier() {
        return new GoogleTokenVerifier();
    }

    @Bean
    public PasswordUpdater passwordUpdater() {
        return new PasswordUpdater();
    }

    @Bean
    public LdapContextSource ldapContextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:389");
        contextSource.setBase("dc=example,dc=com");
        contextSource.setUserDn("cn=Manager,dc=example,dc=com");
        contextSource.setPassword("12345");
        return contextSource;
    }
}
