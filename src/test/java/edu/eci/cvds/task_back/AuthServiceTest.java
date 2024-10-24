package edu.eci.cvds.task_back;

import edu.eci.cvds.task_back.Auth.AuthResponse;
import edu.eci.cvds.task_back.Auth.LoginRequest;
import edu.eci.cvds.task_back.Auth.RegisterRequest;
import edu.eci.cvds.task_back.Domain.Role;
import edu.eci.cvds.task_back.Domain.User;
import edu.eci.cvds.task_back.Repositories.mysql.UserMySqlRepository;
import edu.eci.cvds.task_back.Services.AuthService;
import edu.eci.cvds.task_back.Services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserMySqlRepository userRepository; // Mock del repositorio de usuarios

    @Mock
    private JwtService jwtService; // Mock del servicio JWT

    @Mock
    private PasswordEncoder passwordEncoder; // Mock del codificador de contraseñas

    @Mock
    private AuthenticationManager authenticationManager; // Mock del manejador de autenticación

    @InjectMocks
    private AuthService authService; // Instancia de AuthService con los mocks inyectados

    @BeforeEach
    public void setUp() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Prueba el registro de un nuevo usuario con un correo electrónico ya existente.
     */
    @Test
    public void testEmailAlreadyUsed() {
        // Crear un objeto User con un correo electrónico ya existente
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPasswd("Password1!");

        // Configurar el comportamiento del mock para simular un correo electrónico ya existente
        when(userRepository.findByEmail("existing@example.com")).thenReturn(new User());

        // Verificar que se lanza una excepción al intentar crear el usuario
        Exception exception = assertThrows(Exception.class, () -> {
            authService.createUser(user);
        });

        assertEquals("The email has already been used", exception.getMessage());
    }

    /**
     * Prueba el registro de un nuevo usuario con datos válidos.
     */
    @Test
    public void testValidData() throws Exception {
        // Crear un objeto User con datos válidos
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPasswd("Password1!");

        // Configurar el comportamiento del mock para el repositorio y el codificador de contraseñas
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(null);
        when(passwordEncoder.encode("Password1!")).thenReturn("encodedPassword");

        // Ejecutar el método de creación de usuario
        authService.createUser(user);

        // Verificar que el método de creación de usuario en el repositorio fue llamado
        verify(userRepository, times(1)).createUser(user);

        // Verificar que la contraseña fue codificada correctamente
        assertEquals("encodedPassword", user.getPasswd());
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        // Preparar
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@example.com");
        request.setPasswd("Password1!$");

        User user = new User("user1", "user1@example.com", "encodedPassword", Role.USER);
        user.setId("user1-id");  // Establece un ID ficticio para el usuario

        // Configura el mock para devolver `null` la primera vez y el usuario la segunda vez
        when(userRepository.findByUsername(request.getUsername()))
                .thenReturn(null)  // Primera llamada
                .thenReturn(user);  // Segunda llamada

        when(userRepository.findByEmail(request.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(request.getPasswd())).thenReturn("encodedPassword");
        when(jwtService.getToken(any(User.class))).thenReturn("jwtToken");

        // Ejecutar
        AuthResponse response = authService.register(request);

        // Verificar
        verify(userRepository, times(2)).findByUsername(request.getUsername());
        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode(request.getPasswd());
        verify(userRepository, times(1)).createUser(any(User.class));
        verify(jwtService, times(1)).getToken(any(User.class));

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("user1-id", response.getUserId());
    }

    @Test
    public void testUsernameAlreadyTaken() {
        // Preparar
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1"); // Nombre de usuario que ya está tomado
        request.setEmail("user1@example.com");
        request.setPasswd("Password1@"); // Contraseña válida según tu lógica

        // Simular que el nombre de usuario ya está registrado
        when(userRepository.findByUsername(request.getUsername())).thenReturn(new User());

        // Ejecutar y Verificar
        Exception exception = assertThrows(Exception.class, () -> {
            authService.register(request);
        });

        // Asegurarse de que se lanza la excepción correcta
        assertEquals("Username is already taken!", exception.getMessage());

        // Verificaciones adicionales
        verify(userRepository, times(1)).findByUsername(request.getUsername());
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).createUser(any(User.class));
        verify(jwtService, never()).getToken(any(User.class));
    }

    @Test
    public void testInvalidPassword() {
        // Preparar
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@example.com");
        request.setPasswd("invalid");

        // Ejecutar y Verificar
        Exception exception = assertThrows(Exception.class, () -> {
            authService.register(request);
        });

        assertEquals("Password must be at least 8 characters long, with one uppercase letter, one number, and one special character!", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).createUser(any(User.class));
        verify(jwtService, never()).getToken(any(User.class));
    }

    @Test
    public void testMissingFields() {
        // Preparar
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername(""); // Campo de nombre de usuario vacío
        request1.setEmail("user1@example.com");
        request1.setPasswd("Password1!");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("user1");
        request2.setEmail(""); // Campo de email vacío
        request2.setPasswd("Password1!");

        RegisterRequest request3 = new RegisterRequest();
        request3.setUsername("user1");
        request3.setEmail("user1@example.com");
        request3.setPasswd(""); // Campo de contraseña vacío

        // Ejecutar y verificar
        Exception exception1 = assertThrows(Exception.class, () -> {
            authService.register(request1);
        });
        assertEquals("Username is required!", exception1.getMessage());

        Exception exception2 = assertThrows(Exception.class, () -> {
            authService.register(request2);
        });
        assertEquals("Email is required!", exception2.getMessage());

        Exception exception3 = assertThrows(Exception.class, () -> {
            authService.register(request3);
        });
        assertEquals("Password is required!", exception3.getMessage());

        // Verificar que no se llamaron los métodos del repositorio
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).createUser(any(User.class));
        verify(jwtService, never()).getToken(any(User.class));
    }

    @Test
    public void testGettersAndSettersLoginRequest() {
        // Crear una instancia de LoginRequest
        LoginRequest loginRequest = new LoginRequest();

        // Establecer valores
        loginRequest.setUsername("testUser");
        loginRequest.setPasswd("testPassword");

        // Verificar los valores a través de los getters
        assertEquals("testUser", loginRequest.getUsername(), "El nombre de usuario debería ser 'testUser'");
        assertEquals("testPassword", loginRequest.getPasswd(), "La contraseña debería ser 'testPassword'");
    }
}
