package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFollowRepository extends JpaRepository<Follower,Long> {
    Follower findByUsernameAndFollowedUsername(String username, String followed_username);
}
