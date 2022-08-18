package io.github.gabrielcoelho.quarkussocial.domain.model;

import io.github.gabrielcoelho.quarkussocial.rest.dto.CreateUserRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    public User(CreateUserRequest userRequest){
        this.name = userRequest.getName();
        this.age = userRequest.getAge();
    }
}
