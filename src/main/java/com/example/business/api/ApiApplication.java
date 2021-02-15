package com.example.business.api;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.PriceReductionDTO;
import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.PriceReduction;
import com.example.business.api.model.Supplier;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@SpringBootApplication
@EnableAsync
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(ItemDTO.class, Item.class, "UpdateItemMapping")
				.setPropertyCondition(Conditions.isNotNull());

		modelMapper.typeMap(ItemDTO.class, Item.class, "UpdateItemMapping").addMappings(mapper -> {
			mapper.skip(Item::setPriceReductions);
			mapper.skip(Item::setSuppliers);
			mapper.skip(Item::setCreator);
			mapper.skip(Item::setDeactivationReasons);
		});

		modelMapper.typeMap(ItemDTO.class, Item.class, "SaveItemMapping").addMappings(mapper -> {
			mapper.when(Conditions.isNotNull()).map(ItemDTO::getState, Item::setState);
			mapper.when(Conditions.isNotNull()).map(ItemDTO::getCreationDate, Item::setCreationDate);
		});

		modelMapper.typeMap(SupplierDTO.class, Supplier.class, "UpdateSupplierMapping")
				.setPropertyCondition(Conditions.isNotNull());

		modelMapper.typeMap(SupplierDTO.class, Supplier.class, "UpdateSupplierMapping")
				.addMappings(mapper -> {
					mapper.skip(Supplier::setItems);
		});

		modelMapper.typeMap(SupplierDTO.class, Supplier.class, "SaveSupplierMapping")
				.addMappings(mapper -> {});

		modelMapper.typeMap(PriceReductionDTO.class, PriceReduction.class, "UpdatePriceReductionMapping")
				.setPropertyCondition(Conditions.isNotNull());

		modelMapper.typeMap(PriceReductionDTO.class, PriceReduction.class, "UpdateItemMapping")
				.addMappings(mapper -> mapper.skip(PriceReduction::setItem));

		modelMapper.typeMap(PriceReductionDTO.class, PriceReduction.class, "SavePriceReductionMapping")
				.addMappings(mapper -> mapper.when(Conditions.isNotNull())
						.map(PriceReductionDTO::getStartDate, PriceReduction::setStartDate));

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

			CorsConfiguration corsConfig = new CorsConfiguration().applyPermitDefaultValues();
			corsConfig.addAllowedMethod("DELETE");
			corsConfig.addAllowedMethod("POST");
			corsConfig.addAllowedMethod("PUT");
			corsConfig.addAllowedMethod("OPTIONS");
			http.cors().configurationSource(request -> corsConfig);
		}
	}
}
