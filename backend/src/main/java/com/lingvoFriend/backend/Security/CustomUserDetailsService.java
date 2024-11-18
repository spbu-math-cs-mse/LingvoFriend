package com.lingvoFriend.backend.Security;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.RoleModel;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// in this Service we override the loadUserByUsername method
// it gets the data from database that we can later use to login
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new User(
                userModel.getUsername(),
                userModel.getPassword(),
                MapRolesToAuthorities(userModel.getRoles()));
    }

    private Collection<GrantedAuthority> MapRolesToAuthorities(List<RoleModel> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }
}
