package com.backend_sistema_clinico.user.controller;


import com.backend_sistema_clinico.user.dto.ActualizarUsuarioRequest;
import com.backend_sistema_clinico.user.dto.CrearUsuarioRequest;
import com.backend_sistema_clinico.user.dto.UserDTO;
import com.backend_sistema_clinico.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> listAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UserDTO> crear(@RequestBody @Valid CrearUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.crear(request));
    }

}
