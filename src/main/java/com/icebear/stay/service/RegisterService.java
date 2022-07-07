package com.icebear.stay.service;


import com.icebear.stay.exception.UserAlreadyExistException;
import com.icebear.stay.model.Authority;
import com.icebear.stay.model.User;
import com.icebear.stay.model.UserRole;
import com.icebear.stay.repository.AuthorityRepository;
import com.icebear.stay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public RegisterService(UserRepository userRepository, AuthorityRepository authorityRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;

    }

    // If we meet an error, @Isolation.SERIALIZABLE automatically helps us roll back
    // for both user and authority table, insertion will succeed together or fail together
    @Transactional(isolation = Isolation.SERIALIZABLE)
    // role information comes from url
    public void add(User user, UserRole role) throws UserAlreadyExistException {
        // if a user try to reuse an existing username
        if (userRepository.existsById(user.getUsername())) {
            // after throw this exception,
            // CustomExceptionHandler will catch this exception
            // and return the response
            throw new UserAlreadyExistException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        authorityRepository.save(new Authority(user.getUsername(), role.name()));// role.name(): convert an Enum to String
    }
}
