package io.github.gabrielcoelho.quarkussocial.rest.dto;

import io.github.gabrielcoelho.quarkussocial.domain.model.Follower;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowerResponse {

    private Long id;
    private String name;

    public FollowerResponse(Follower follower) {
        this(follower.getId(), follower.getFollower().getName());
    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
