package com.example.takehome.dto.graphql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for GraphQL continent request body.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContinentGqlRequestBody {
  private String query;
}
