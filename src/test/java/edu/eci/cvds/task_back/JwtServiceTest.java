package edu.eci.cvds.task_back;

import edu.eci.cvds.task_back.Services.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    /**
     * Configuración inicial para cada prueba. Se inicializan los objetos necesarios.
     */
    @BeforeEach
    void setUp() {
        // Inicializa los mocks
        MockitoAnnotations.openMocks(this);

        // Configura un usuario con un rol para las pruebas
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        List<GrantedAuthority> authorities = Collections.singletonList(authority);
        userDetails = new User("testUser", "password", authorities);
    }

    /**
     * Prueba para verificar que el método getToken() genera un token.
     */
    @Test
    void testGetToken() {
        // Generar un token para el usuario configurado
        String token = jwtService.getToken(userDetails);

        // Verifica que el token no sea nulo
        assertNotNull(token);
    }

    /**
     * Prueba para verificar que el método getUsernameFromToken() extrae el nombre de usuario del token.
     */
    @Test
    void testGetUsernameFromToken() {
        // Generar un token para el usuario configurado
        String token = jwtService.getToken(userDetails);

        // Extraer el nombre de usuario del token
        String username = jwtService.getUsernameFromToken(token);

        // Verifica que el nombre de usuario extraído coincida con el esperado
        assertEquals("testUser", username);
    }

    /**
     * Prueba para verificar que el método isTokenValid() valida correctamente el token.
     */
    @Test
    void testIsTokenValid() {
        // Generar un token para el usuario configurado
        String token = jwtService.getToken(userDetails);

        // Validar el token generado
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Verifica que el token sea válido
        assertTrue(isValid);
    }

    /**
     * Prueba para verificar que el método getClaim() extrae un claim específico del token.
     */
    @Test
    void testGetClaim() {
        // Generar un token para el usuario configurado
        String token = jwtService.getToken(userDetails);

        // Extraer el claim (en este caso, el nombre de usuario) del token
        String username = jwtService.getClaim(token, Claims::getSubject);

        // Verifica que el nombre de usuario extraído coincida con el esperado
        assertEquals("testUser", username);
    }
}
