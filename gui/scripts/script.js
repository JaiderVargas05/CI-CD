/*
*Funcion para cargar las tareas
*/
function loadTask() {
    fetch("http://localhost:80/taskManager/getTasks", {
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        method: "GET"
    })
    .then(function (res) {
        if (!res.ok) {
            throw new Error('Error en la solicitud: ' + res.status);
        }
        return res.json();  
    })
    .then(function (data) {
        console.log(data);
        let taskList = [];
        let taskhtml = '';
        for(let i=0; i<data.length;i++){
            const task = data[i];
            let isCompleted = task.isCompleted ? 'COMPLETED' : '';
            let buttonCheck = task.isCompleted 
            ? `<input type="checkbox" class="task-checkbox" onclick="disabledButton('${task.id}')" checked disabled />` 
            : `<input type="checkbox" class="task-checkbox" onclick="disabledButton('${task.id}')" />`;

            
            let priorityColor;
            switch (task.priority) {
                case '1': priorityColor = '#afd1c6'; break;   
                case '2': priorityColor = '#15505d'; break; 
                case '3': priorityColor = '#acfb03'; break; 
                case '4': priorityColor = '#3c895f'; break;  
                case '5': priorityColor = '#7bd179'; break; 
                default: priorityColor = 'gray';
            }

            taskhtml+= `
            <div class="task ${isCompleted}">
                ${buttonCheck}
                <div>
                <div class="priority-indicator"></div>
                <h2>${task.name}</h2> 
                </div> 
                <p>${task.description}</p>
                <p>Creation date: ${task.creationDate}</p>
                <p>Due date: ${task.dueDate}</p>
                <p>Difficulty: ${task.difficulty}</p>
                <p>Average Time: ${task.estimatedTime}</p>
                <button class="delete-button" onclick="deleteTask('${task.id}')"><i class="fas fa-trash-alt"></i></button>
            </div>` 
        }
        document.getElementById("task-container").innerHTML = taskhtml;
    })
    .catch(function (error) {
        console.log('Error:', error);
    });
}

/*
*Funcion para añadir una tarea
*/
function addTask(){
    const taskName = document.getElementById("taskTitle").value;
    const description = document.getElementById("taskDescription").value;
    const date = document.getElementById("taskDueDate").value;
    const difficultyTask = document.getElementById("taskDifficulty").value;
    const priorityTask = document.getElementById("taskPriority").value;
    const time = document.getElementById("averageTime").value;
    console.log(difficultyTask);
    console.log(priorityTask);
    console.log(time);
    if (!taskName || !description || !date || !difficultyTask || !priorityTask || !time) {
        alert('Please fill all the fields');
        return;
    }
    if(taskName.length > 30){
        alert('The title is too long');
        return;
    }
    if (description.length > 50) {
        alert('The description is too long');
        return;
    }
    if (time < 0){
        alert('You cannot add negative time');
        return;
   }
    let currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0);
    let selectedDate = new Date(date);
    if (selectedDate < currentDate) {
        alert('The due date must be greater than the current date');
        return;
    }; 
    fetch("http://localhost:80/taskManager/saveTask",
        {
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            method: "POST",
            body: JSON.stringify({
                name: taskName,
                description: description,
                dueDate: date,
                difficulty: difficultyTask,
                priority: priorityTask,
                estimatedTime: time
            })
        })
        .then(function (res) { console.log(res); loadTask(); })
        .catch(function (res) { console.log(res) })
        //loadTask();
    }
    /*
    *Funcion para eliminar tareas
    */
    function deleteTask(taskId) {
        fetch(`http://localhost:80/taskManager/delete?id=${taskId}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
        .then(function (res) {
            if (res.ok) {
                console.log('Task deleted successfully');
                loadTask();
            } else {
                console.log('Failed to delete task');
            }
        })
        .catch(function (error) {
            console.log('Error:', error);
        });
    }
    /*
    *Funcion para marcar una tarea como completada
    */
    function disabledButton(id){
        console.log(id);
        fetch(`http://localhost:80/taskManager/markTaskAsCompleted?id=${id}`, {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
        .then(function (res) {
            if (res.ok) {
                loadTask();
            } else {
                console.log('Failed to delete task');
            }
        })
        .catch(function (error) {
            console.log('Error:', error);
        });
    }
    function generateRandomTasks(){
        fetch(`http://localhost:80/taskManager/generateTasks`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
        .then(function (res) {
            if (res.ok) {
                loadTask();
            } else {
                console.log('Failed to delete task');
            }
        })
        .catch(function (error) {
            console.log('Error:', error);
        });
    }
    
    window.addTask = addTask;
    window.deleteTask = deleteTask;
    $(document).ready(function () {
        loadTask();
    });

