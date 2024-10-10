package edu.eci.cvds.task_back.Services;

import edu.eci.cvds.task_back.Domain.Task;
import edu.eci.cvds.task_back.Domain.User;
import edu.eci.cvds.task_back.Repositories.TaskRepository;
import edu.eci.cvds.task_back.Repositories.mysql.UserMySqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class UserService {
    private TaskRepository taskRepository;
    private UserMySqlRepository userRepository;
    /**
     * Constructor que inyecta el repositorio de tareas mediante dependencia.
     * @param taskRepository El repositorio que se utilizará para la gestión de tareas.
     */
    @Autowired
    public UserService(TaskRepository taskRepository, UserMySqlRepository userRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * Recupera una tarea por su ID.
     * @param id El identificador de la tarea.
     * @return La tarea encontrada o {@code null} si no existe.
     */
    public Task getTask(String id){
        return taskRepository.findTaskById(id);
    }

    /**
     * Recupera todas las tareas almacenadas.
     * @return Lista de todas las tareas almacenadas.
     */
    public List<Task> getTasks(){
        return taskRepository.findAllTasks();
    }
    public List<Task> getTasksByUser(String userId){
        User user = userRepository.getUser(userId);
        return taskRepository.findTasksByUser(user);
    }

    /**
     * Guarda una nueva tarea en el repositorio.
     * @param task La tarea a guardar.
     */
    public void saveTaskByUser(String userId, Task task) throws Exception{
        try{
            User user = this.userRepository.getUser(userId);
            if(user==null) throw new Exception("The user doesn't exist");
            task.setUser(user);
            taskRepository.saveTask(task);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
    public void saveTask(Task task){
        taskRepository.saveTask(task);
    }

    /**
     * Marca una tarea como completada cambiando su estado.
     * @param id El identificador de la tarea a marcar como completada.
     */
    public void markTaskAsCompleted(String id){
        Task taskRepo = getTask(id);
        if (taskRepo != null){
            taskRepo.setIsCompleted(true);
            taskRepository.updateTask(taskRepo);
        }
    }

    /**
     * Elimina una tarea del repositorio.
     * @param id El identificador de la tarea a eliminar.
     */
    public void deleteTask(String id){
        Task taskRepo = getTask(id);
        if (taskRepo != null){
            taskRepository.deleteTask(taskRepo);
        }
    }

    /**
     * genera un número aleatorio de tareas (entre 100 y 1000)
     * y asigna valores aleatorios para sus propiedades.
     */
    public void RandomTask(String idUser) throws Exception {
        Random random = new Random();
        int randomTasks = random.nextInt(100, 1001);
        String[] difficulties = {"High", "Middle", "Low"};

        for (int i = 0; i < randomTasks; i++) {
            try{
                String name = "Task" + (i + 1);
                String description = "Description" + (i + 1);
                String dueDate = LocalDate.now().plusDays(random.nextInt(30) + 1).toString();
                String difficulty = difficulties[random.nextInt(difficulties.length)];
                Integer priority = random.nextInt(1, 6);
                double estimatedTime = random.nextDouble() * 10;
                Task task = new Task(name, description, dueDate, difficulty, priority, estimatedTime);
                saveTaskByUser(idUser, task);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }

        }
    }

    public void createUser(User user) throws Exception{
        try{

            if(userRepository.findByEmail(user.getEmail())!=null) throw new Exception("The email has already been used");
            user.setPasswd(passwordEncoder.encode(user.getPasswd())); //encripta la contraseña
            this.userRepository.createUser(user);
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }


    public boolean deleteUser(String userId) throws Exception {

        try{
            User user = this.userRepository.getUser(userId);
            if(user!=null){
                this.userRepository.deleteUser(user);
                return true;
            }
            return false;
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public boolean modifyUser(User user) throws Exception {
        try{
            User oldUser = this.userRepository.getUser(user.getId());
            if (oldUser!=null){
                this.userRepository.modifyUser(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String authentication(String email, String passwd) throws Exception {
        try{
            User user = userRepository.findByEmail(email);
            if (user != null && passwordEncoder.matches(passwd, user.getPasswd())) {
                return user.getId();
            }
            else{
                throw new Exception("User doesn't exist or invalid credentials");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
    public String getUsername(String id) throws Exception {
        try{
            String user = userRepository.getUserName(id);
            if (user != null) {
                return user;
            }
            else{
                throw new Exception("User doesn't exist");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
