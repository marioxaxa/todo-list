package br.com.marioxaxa.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private UUID userid;

    @Column(length = 50)
    private String tittle;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String priority;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setTittle(String tittle) throws Exception{
        System.out.println(tittle);
        if(tittle.length() > 50){
            throw new Exception("O titulo deve conter no maximo 50 caracteres");
        }
        this.tittle = tittle;
    }
}
