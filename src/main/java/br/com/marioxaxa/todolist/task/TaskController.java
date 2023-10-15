package br.com.marioxaxa.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.marioxaxa.todolist.utils.Utils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskRequest, HttpServletRequest request){
        var userId = request.getAttribute("userId");
        taskRequest.setUserid((UUID)userId);

        var currentDate = LocalDateTime.now();

        if(currentDate.isAfter(taskRequest.getStartAt()) || currentDate.isAfter(taskRequest.getEndAt())){
            return ResponseEntity.status(400).body("Data de inicio ou termino invalida");
        }

        if(taskRequest.getStartAt().isAfter(taskRequest.getEndAt())){
            return ResponseEntity.status(400).body("Data de inicio deve vir antes da data de termino");
        }


        var task = this.taskRepository.save(taskRequest);

        return ResponseEntity.status(201).body(task);
    }
    
    @GetMapping("/")
    public List<TaskModel> getUserTasks(HttpServletRequest request){
        var userId = request.getAttribute("userId");
        var tasks = this.taskRepository.findByUserid((UUID)userId);
        return tasks;
    }

    @PutMapping("/{taskId}")
    public ResponseEntity updateTask(@RequestBody TaskModel taskRequest, HttpServletRequest request, @PathVariable UUID taskId){

        var task = this.taskRepository.findById(taskId).orElse(null);

        if(task == null){
            return ResponseEntity.status(400).body("Tarefa não encontrada");
        }

        var userId = request.getAttribute("userId");
        if(!task.getUserid().equals(userId)){
            return ResponseEntity.status(400).body("Usuario não possui acesso para alterar task de outro usuario");
        }

        Utils.copyNonNullProperties(taskRequest, task);

        var updatedTask = this.taskRepository.save(task);

        return ResponseEntity.status(200).body(updatedTask);
    }

}
