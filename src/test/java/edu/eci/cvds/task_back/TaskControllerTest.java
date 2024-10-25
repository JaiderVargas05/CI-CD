package edu.eci.cvds.task_back;


import edu.eci.cvds.task_back.Domain.Task;
import edu.eci.cvds.task_back.Domain.User;
import edu.eci.cvds.task_back.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService taskService;

    @InjectMocks
    private edu.eci.cvds.task_back.Controller.UserController taskController;

    private Task task1;
    private Task task2;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        new User("Claudia","clau@mail.com", "123");
        task1 = new Task( "Test Task 1", "Description 1", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),"high",3,3.8);
        task2 = new Task( "Test Task 2", "Description 2", LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),"high",3,3.8);
    }

    @Test
    void testSaveTask() throws Exception {
        // Realizar una solicitud POST a la ruta "/taskManager/saveTask" con contenido JSON que representa una tarea
        mockMvc.perform(post("/taskManager/saveTask")
                        .contentType(MediaType.APPLICATION_JSON) // Especificar el tipo de contenido como JSON
                        .content("{\"id\":\"1\", \"name\":\"Test Task 1\", \"description\":\"Description 1\", \"dueDate\":\"2024-12-31\"}")) // Contenido JSON de la tarea
                .andExpect(status().isOk()); // Esperar que la respuesta tenga un estado HTTP 200 (OK)

        // Verificar que el método saveTask del taskService fue llamado una vez con cualquier objeto Task
        verify(taskService, times(1)).saveTask(any(Task.class));
    }

    @Test
    void testMarkTaskAsCompleted() throws Exception {
        // Realizar una solicitud PATCH a la ruta "/taskManager/markTaskAsCompleted" con el parámetro "id" establecido en "1"
        mockMvc.perform(patch("/taskManager/markTaskAsCompleted")
                        .param("id", "1")) // Agregar el parámetro de la tarea a marcar como completada
                .andExpect(status().isOk()); // Esperar que la respuesta tenga un estado HTTP 200 (OK)

        // Verificar que el método markTaskAsCompleted del taskService fue llamado una vez con el ID de la tarea "1"
        verify(taskService, times(1)).markTaskAsCompleted("1");
    }

    @Test
    void testDeleteTask() throws Exception {
        // Realizar una solicitud DELETE a la ruta "/taskManager/delete" con el parámetro "id" establecido en "1"
        mockMvc.perform(delete("/taskManager/delete")
                        .param("id", "1")) // Agregar el parámetro del ID de la tarea que se desea eliminar
                .andExpect(status().isOk()); // Esperar que la respuesta tenga un estado HTTP 200 (OK)

        // Verificar que el método deleteTask del taskService fue llamado una vez con el ID de la tarea "1"
        verify(taskService, times(1)).deleteTask("1");
    }

    @Test
    void testGetTasks() throws Exception {
        // Crear una lista de tareas simuladas
        List<Task> tasks = Arrays.asList(task1, task2);
        // Configurar el comportamiento simulado del servicio para devolver la lista de tareas
        when(taskService.getTasks()).thenReturn(tasks);

        // Realizar una solicitud GET a la ruta "/taskManager/getTasks"
        mockMvc.perform(get("/taskManager/getTasks"))
                .andExpect(status().isOk()) // Esperar que la respuesta tenga un estado HTTP 200 (OK)
                .andExpect(jsonPath("$[0].name").value("Test Task 1")) // Verificar que el nombre de la primera tarea sea "Test Task 1"
                .andExpect(jsonPath("$[1].name").value("Test Task 2")); // Verificar que el nombre de la segunda tarea sea "Test Task 2"

        // Verificar que el método getTasks del taskService fue llamado una vez
        verify(taskService, times(1)).getTasks();
    }

}