package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/permissions")
public class PermissionController {

    @PostMapping("/assign")
    public ResponseEntity<String> assignPermissionToUser(@RequestBody List<PermissionDTO> permissionDTOList) {

    }

    @PostMapping("/assign-single")
    public ResponseEntity<String> assignSinglePermssionToUser(@RequestBody PermissionDTO permissionDTO) {

    }
}
