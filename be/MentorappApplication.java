package com.obss.mentorapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.obss.mentorapp.repository")  // JPA repository'leri
@EnableElasticsearchRepositories(basePackages = "com.obss.mentorapp.elastic")  // Elasticsearch repository'leri
public class MentorappApplication {
	public static void main(String[] args) {
		SpringApplication.run(MentorappApplication.class, args);
	}
}


