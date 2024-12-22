package ru.lingvofriend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private String username;
    private List<String> goals;
    private List<String> interests;
    private String cefrLevel;
    private String dialect = "british";
}
