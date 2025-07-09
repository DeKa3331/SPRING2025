package com.umcsuser.bookstore.controller;

import com.umcsuser.bookstore.dto.LoginRequest;
import com.umcsuser.bookstore.dto.LoginResponse;
import com.umcsuser.bookstore.dto.OrderRequest;
import com.umcsuser.bookstore.dto.UserRequest;
import com.umcsuser.bookstore.models.Order;
import com.umcsuser.bookstore.models.User;
import com.umcsuser.bookstore.repository.UserRepository;
import com.umcsuser.bookstore.security.JwtUtil;
import com.umcsuser.bookstore.service.OrderService;
import com.umcsuser.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLogin(),
                            loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        LoginResponse responseBody = new LoginResponse(token);
        return ResponseEntity.ok(responseBody);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest req) {
        try {
            userService.register(req);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Registered successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @PostMapping("/order")
    public ResponseEntity<Order> orderBook(
            @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal UserDetails userDetails)
    {
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie znaleziony: " + login));
        Order rental = orderService.order(orderRequest.getBookId(),orderRequest.getQuantity(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> softDeleteUser(@PathVariable String id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{id}/roles")
    public ResponseEntity<?> addRole(@PathVariable String id, @RequestBody String roleName) {
        userService.addRoleToUser(id, roleName);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}/roles")
    public ResponseEntity<?> removeRole(@PathVariable String id, @RequestBody String roleName) {
        userService.removeRoleFromUser(id, roleName);
        return ResponseEntity.ok().build();
    }
}