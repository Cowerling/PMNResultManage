package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.DuplicateUserException;

public interface UserRepository {
    User findUserByName(String name);
    User findUserById(Long id);
    void saveUser(User user) throws DuplicateUserException;
    void updateUser(User user);
}
