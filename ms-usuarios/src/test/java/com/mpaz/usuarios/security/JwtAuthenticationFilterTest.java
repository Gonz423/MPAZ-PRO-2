package com.mpaz.usuarios.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private Authentication ejecutar(String header) throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        if (header != null) req.addHeader("Authorization", header);
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(req, new MockHttpServletResponse(), chain);
        assertThat(chain.getRequest()).isNotNull(); // la cadena siempre continua
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Test
    @DisplayName("un token valido autentica al usuario con su rol")
    void tokenValido() throws Exception {
        when(jwtService.isTokenValid("abc")).thenReturn(true);
        when(jwtService.extractUsername("abc")).thenReturn("docente");
        when(jwtService.extractRol("abc")).thenReturn("docente");

        Authentication auth = ejecutar("Bearer abc");

        assertThat(auth.getName()).isEqualTo("docente");
        assertThat(auth.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_DOCENTE");
    }

    @Test
    @DisplayName("un token valido sin rol autentica sin autoridades")
    void tokenSinRol() throws Exception {
        when(jwtService.isTokenValid("abc")).thenReturn(true);
        when(jwtService.extractUsername("abc")).thenReturn("x");
        when(jwtService.extractRol("abc")).thenReturn(null);

        assertThat(ejecutar("Bearer abc").getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("sin header, con otro esquema o con token invalido no autentica")
    void sinAutenticacion() throws Exception {
        when(jwtService.isTokenValid("malo")).thenReturn(false);

        assertThat(ejecutar(null)).isNull();
        assertThat(ejecutar("Basic xyz")).isNull();
        assertThat(ejecutar("Bearer   ")).isNull();
        assertThat(ejecutar("Bearer malo")).isNull();
        verify(jwtService, never()).extractUsername(any());
    }
}
