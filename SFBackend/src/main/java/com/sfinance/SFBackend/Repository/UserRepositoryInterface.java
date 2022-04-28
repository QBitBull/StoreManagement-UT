package com.sfinance.SFBackend.Repository;

import com.sfinance.SFBackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryInterface extends JpaRepository<User, Long> {

    User findUserByUsername(String username);
    User findUserByEmail(String email);
    void deleteUserByUsername(String username);
}
