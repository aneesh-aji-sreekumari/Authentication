package com.example.authentication.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Role extends BaseModel{
    private String name;
    public Role(String name){
        this.name = name;
    }


}
