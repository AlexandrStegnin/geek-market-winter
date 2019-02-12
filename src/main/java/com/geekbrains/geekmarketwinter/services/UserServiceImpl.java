package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.entites.SystemUser;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ROLE_EMPLOYEE;
import static com.geekbrains.geekmarketwinter.config.support.Constants.ROLE_PREFIX;

@Service
public class UserServiceImpl implements UserService {
	private UserRepository userRepository;
	private RoleService roleService;
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	@Autowired
	public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public User findByUserName(String userName) {
		return userRepository.findOneByUserName(userName);
	}

	@Override
	@Transactional
	public void save(SystemUser systemUser) {
		User user = new User();
		user.setUserName(systemUser.getUserName());
		user.setPassword(passwordEncoder.encode(systemUser.getPassword()));
		user.setFirstName(systemUser.getFirstName());
		user.setLastName(systemUser.getLastName());
		user.setEmail(systemUser.getEmail());
		Set<Role> roles = new HashSet<>();
		roles.add(roleService.findRoleByName(ROLE_PREFIX + ROLE_EMPLOYEE));
		user.setRoles(roles);

		userRepository.save(user);
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userRepository.findOneByUserName(userName);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Override
	public User create(User user) {
		if (user.getRoles().size() == 0) user.setRoles(
				Collections.singleton(roleService.findRoleByName(ROLE_PREFIX + ROLE_EMPLOYEE))
		);
		if (StringUtils.hasText(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		} else {
			user.setPassword(userRepository.findOneByUserName(user.getUserName()).getPassword());
		}
		return userRepository.save(user);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User update(User user) {
		return userRepository.save(user);
	}

	@Override
	public void delete(User user) {
		userRepository.delete(user);
	}
}
