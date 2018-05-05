package com.cowerling.pmn.security;

import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.geodata.security.GeoDataSecurityService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class SecurityUserService implements UserDetailsService {
    private final UserRepository userRepository;

    public SecurityUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        com.cowerling.pmn.domain.user.User user = userRepository.findUserByName(name);
        if (user != null) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));

            if (!GeoDataSecurityService.login(user.getName(), user.getPassword()))
                throw new UsernameNotFoundException("Geo User '" + name + "' not found");

            return new User(user.getName(), user.getPassword(), authorities);
        }

        throw new UsernameNotFoundException("User '" + name + "' not found");
    }
}
