package njt.NJT_Projekat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import njt.NJT_Projekat.security.JwtAuthFilter;
import njt.NJT_Projekat.security.AppUserDetailsService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AppUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        
                // 1. JAVNE RUTE 
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Dozvoli OPTIONS zahtev
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/sale/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/karakteristike").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tipovi-dogadjaja").permitAll()
                        
                // 2. ADMINISTRATORSKE RUTE 
                .requestMatchers(HttpMethod.POST, "/api/sale/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/sale/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/sale/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/rezervacije").hasRole("ADMIN") // Pregled svih
                .requestMatchers(HttpMethod.PUT, "/api/rezervacije/potvrdi/**").hasRole("ADMIN") // Potvrda
                .requestMatchers("/api/korisnici/pretraga").hasRole("ADMIN")
                        
                // 3. KLIJENTSKE RUTE
                .requestMatchers("/api/rezervacije/moje").hasRole("KORISNIK")
                .requestMatchers(HttpMethod.POST, "/api/rezervacije/**").hasRole("KORISNIK")
                .requestMatchers(HttpMethod.DELETE, "/api/rezervacije/**").hasAnyRole("KORISNIK", "ADMIN")
                        
                        
                // 4. SVE OSTALE RUTE
                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Tvoj frontend (npr. React)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
