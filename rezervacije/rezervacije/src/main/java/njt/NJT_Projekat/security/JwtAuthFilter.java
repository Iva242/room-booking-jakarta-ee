package njt.NJT_Projekat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;


    public JwtAuthFilter(JwtService jwtService, AppUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        //izvlacimo "Authorization" zaglavlje iz HTTP zahteva
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String korisnickoIme;

        //ako zaglavlje ne postoji ili ne pocinje sa "Bearer ", preskacemo filter i idemo dalje
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //izlacimo sam token i preskacemo prvih 7 karaktera jer  Bearer  zauzima tolik
        jwt = authHeader.substring(7);
        
        try {
            //izvlacimo korIme iz tokena
            korisnickoIme = jwtService.extractUsername(jwt);

            // ako imamo korIme , a korisnik još uvek nije autentifikovan u trenutnoj niti (contextu)
            if (korisnickoIme != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                //citamo detalje o korisniku iz baze preko našeg servisa
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(korisnickoIme);

                // ako je token validan, pakujemo podatke o korisniku i ulogama u Springov Authentication objekat
                if (jwtService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // smestamo korisnika u Security Context - od ovog momenta Spring zna ko je korisnik i sta sme da radi
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // ako je token istekao ili je nevalidan, logujemo gresku
            logger.error("Nije moguće postaviti autentifikaciju korisnika: " + e.getMessage());
        }

        // prosledjivanje zhteva sledecem filteru u lancu
        filterChain.doFilter(request, response);
    }
}
