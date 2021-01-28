package com.example.business.api;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;
import com.example.business.api.security.AuthenticationFacade;
import com.example.business.api.security.JWTAuthorizationFilter;
import com.example.business.api.security.UserAuthentication;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(ItemDTO.class, Item.class, "UpdateItemMapping").setPropertyCondition(Conditions.isNotNull());

		modelMapper.typeMap(ItemDTO.class, Item.class, "UpdateItemMapping").addMappings(mapper -> {
			mapper.skip(Item::setPriceReductions);
			mapper.skip(Item::setSuppliers);
		});

		modelMapper.typeMap(ItemDTO.class, Item.class, "SaveItemMapping").addMappings(mapper -> {
			mapper.when(Conditions.isNotNull()).map(ItemDTO::getState, Item::setState);
			mapper.when(Conditions.isNotNull()).map(ItemDTO::getCreationDate, Item::setCreationDate);
		});

		return modelMapper;
	}

	@Bean
	public AuthenticationFacade authenticationFacade() {
		return new UserAuthentication();
	}

	@EnableWebSecurity
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
					.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
					.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/login").permitAll()
					.and()
					.authorizeRequests()
					.antMatchers("/h2-console/**").permitAll()
					.anyRequest().authenticated();
			http.headers().frameOptions().disable();
		}
	}
}
