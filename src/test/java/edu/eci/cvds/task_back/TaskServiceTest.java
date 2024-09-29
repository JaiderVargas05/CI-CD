package edu.eci.cvds.task_back;


import edu.eci.cvds.task_back.Domain.Task;
import edu.eci.cvds.task_back.Repositories.TaskRepository;
import edu.eci.cvds.task_back.Services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTask() {
        // Crear un objeto Task con ID "1" y otros detalles
        Task task = new Task("1", "Test Task", "Description", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Configurar el comportamiento del mock para devolver el task creado al buscar por ID "1"
        when(taskRepository.findTaskById("1")).thenReturn(task);

        // Llamar al método getTask en el servicio para obtener la tarea por ID
        Task result = taskService.getTask("1");

        // Verificar que el resultado no es nulo
        assertNotNull(result);
        // Comprobar que el ID de la tarea resultante es "1"
        assertEquals("1", result.getId());
        // Verificar que el nombre de la tarea resultante es "Test Task"
        assertEquals("Test Task", result.getName());
    }

    @Test
    void testGetTasks() {
        // Crear dos objetos Task con IDs y otros detalles
        Task task1 = new Task("1", "Test Task 1", "Description 1", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Task task2 = new Task("2", "Test Task 2", "Description 2", LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Agrupar las tareas en una lista
        List<Task> tasks = Arrays.asList(task1, task2);

        // Configurar el comportamiento del mock para devolver la lista de tareas al llamar a findAllTasks
        when(taskRepository.findAllTasks()).thenReturn(tasks);

        // Llamar al método getTasks en el servicio para obtener todas las tareas
        List<Task> result = taskService.getTasks();

        // Verificar que el resultado no es nulo
        assertNotNull(result);
        // Comprobar que el tamaño de la lista de tareas resultante es 2
        assertEquals(2, result.size());
        // Verificar que el nombre de la primera tarea es "Test Task 1"
        assertEquals("Test Task 1", result.get(0).getName());
        // Verificar que el nombre de la segunda tarea es "Test Task 2"
        assertEquals("Test Task 2", result.get(1).getName());
    }

    @Test
    void testSaveTask() {
        // Crear un objeto Task con un ID, nombre, descripción y fecha de vencimiento
        Task task = new Task("1", "Test Task", "Description", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Llamar al método saveTask del servicio para guardar la tarea
        taskService.saveTask(task);

        // Verificar que el método saveTask del repositorio fue llamado una vez con el objeto Task
        verify(taskRepository, times(1)).saveTask(task);
    }

    @Test
    void testMarkTaskAsCompleted() {
        // Crear un objeto Task con un ID, nombre, descripción y fecha de vencimiento
        Task task = new Task("1", "Test Task", "Description", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // Inicializar el estado de completado de la tarea como falso
        task.setIsCompleted(false);

        // Configurar el comportamiento del repositorio para que devuelva la tarea cuando se busque por ID
        when(taskRepository.findTaskById("1")).thenReturn(task);

        // Llamar al método markTaskAsCompleted del servicio para marcar la tarea como completada
        taskService.markTaskAsCompleted("1");

        // Verificar que el estado de completado de la tarea ahora es verdadero
        assertTrue(task.getIsCompleted());
        // Verificar que el método updateTask del repositorio fue llamado una vez con la tarea actualizada
        verify(taskRepository, times(1)).updateTask(task);
    }

    @Test
    void testDeleteTask() {
        // Crear un objeto Task con un ID, nombre, descripción y fecha de vencimiento
        Task task = new Task("1", "Test Task", "Description", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Configurar el comportamiento del repositorio para que devuelva la tarea cuando se busque por ID
        when(taskRepository.findTaskById("1")).thenReturn(task);

        // Llamar al método deleteTask del servicio para eliminar la tarea
        taskService.deleteTask("1");

        // Verificar que el método deleteTask del repositorio fue llamado una vez con la tarea correspondiente
        verify(taskRepository, times(1)).deleteTask(task);
    }

    /*Pruebas Lab5*/

    @Test
    void SucessfullIDQuery() {
        // Crear un objeto Task con ID "2" y otros detalles
        Task task = new Task("2", "Test Task", "Description", LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Configurar el comportamiento del mock para devolver el task creado al buscar por ID "2"
        when(taskRepository.findTaskById("2")).thenReturn(task);

        // Llamar al método getTask en el servicio para obtener la tarea por ID
        Task result = taskService.getTask("2");

        // Verificar que el resultado no es nulo
        assertNotNull(result);
        // Comprobar que el ID de la tarea resultante es "2"
        assertEquals("2", result.getId());
    }
    @Test
    void UnsucessfullIDQuery() {


        // Se intenta obtener una tarea que no existe con su ID
        Task result = taskService.getTask("2");

        // Llamar al método getTasks en el servicio para obtener todas las tareas
        List<Task> results = taskService.getTasks();

        // Verificar que el resultado es nulo
        assertEquals(results.size(),0);

        // Verificar que la tarea consultada con el ID no existe
        assertNull(result);
    }

    @Test
    void successfulTaskQuery() {
        // Configurar el comportamiento del mock para devolver una lista vacía inicialmente
        when(taskRepository.findAllTasks()).thenReturn(Collections.emptyList());

        // Llamar al método getTasks en el servicio para obtener todas las tareas
        List<Task> results = taskService.getTasks();

        // Verificar que la lista de tareas está vacía
        assertEquals(0, results.size());

        // Crear un objeto Task con ID "2" y otros detalles
        Task task = new Task("2", "Test Task", "Description", LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Guardar la tarea
        taskService.saveTask(task);

        // Configurar el comportamiento del mock para devolver la tarea creada al buscar por ID "2"
        when(taskRepository.findTaskById("2")).thenReturn(task);

        // Llamar al método getTask en el servicio para obtener la tarea por ID
        Task result = taskService.getTask("2");

        // Verificar que el resultado no es nulo
        assertNotNull(result);

        // Configurar el comportamiento del mock para devolver una lista con la tarea creada
        when(taskRepository.findAllTasks()).thenReturn(Collections.singletonList(task));

        // Llamar al método getTasks en el servicio para obtener todas las tareas
        List<Task> results2 = taskService.getTasks();

        // Verificar que la lista contiene una tarea
        assertEquals(1, results2.size());
    }

    @Test
    void successfulTaskDelete() {

        // Crear un objeto Task con ID "2" y otros detalles
        Task task = new Task("2", "Test Task", "Description", LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Guardar la tarea
        taskService.saveTask(task);

        when(taskRepository.findAllTasks()).thenReturn(Collections.singletonList(task));

        // Llamar al método getTasks en el servicio para obtener todas las tareas
        List<Task> results = taskService.getTasks();

        // Verificar que la lista contiene una tarea
        assertEquals(1, results.size());

        taskService.deleteTask("2");

        // Configurar el comportamiento del mock para devolver una lista con la tarea creada
        when(taskRepository.findAllTasks()).thenReturn(Collections.emptyList());

        List<Task> result2 = taskService.getTasks();

        assertEquals(0,result2.size());
    }

    @Test
    void successfulTaskDeleteAndGetNullwithID() {

        // Crear un objeto Task con ID "2" y otros detalles
        Task task = new Task("2", "Test Task", "Description", LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Guardar la tarea
        taskService.saveTask(task);

        when(taskRepository.findAllTasks()).thenReturn(Collections.singletonList(task));

        // Llamar al método getTasks en el servicio para obtener todas las tareas
        List<Task> results = taskService.getTasks();

        // Verificar que la lista contiene una tarea
        assertEquals(1, results.size());

        taskService.deleteTask("2");

        // Configurar el comportamiento del mock para devolver una lista con la tarea creada
        when(taskRepository.findAllTasks()).thenReturn(Collections.emptyList());

        List<Task> result2 = taskService.getTasks();

        assertEquals(0,result2.size());

        when(taskRepository.findTaskById("2")).thenReturn(null);

        Task taskFinal = taskService.getTask("2");
        assertNull(taskFinal);
    }
}