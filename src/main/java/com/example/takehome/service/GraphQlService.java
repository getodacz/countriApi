package com.example.takehome.service;

import com.example.takehome.dto.graphql.ContinentGqlData;
import com.example.takehome.dto.graphql.ContinentGqlRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
/**
 Service class for making GraphQL API calls to retrieve continent and country data.
 */
@Service
@Slf4j
public class GraphQlService {
  /**
   The GraphQL API URL, configured in application properties.
   */
  @Value("${graphql.api.url}")
  private String graphQlApiUrl;

  /**
   The file path for the GraphQL query, configured in application properties.
   */
  @Value("${graphql.query.filepath}")
  private String graphQlQueryFilepath;

  /**
   Retrieves a {@link ContinentGqlData} object containing data for all continents and their countries.
   @return a {@link ContinentGqlData} object containing data for all continents and their countries
   @throws IOException if an error occurs while reading the GraphQL query file
   */
  public ContinentGqlData getContinentsWithCountries() throws IOException {
    final String graphQlQuery = getGraphQlSchemaFromFileName();
    ContinentGqlRequestBody graphQLRequestBody = new ContinentGqlRequestBody(graphQlQuery);

    WebClient webClient = WebClient.builder().build();

    return webClient.post()
        .uri(this.graphQlApiUrl)
        .bodyValue(graphQLRequestBody)
        .retrieve()
        .bodyToMono(ContinentGqlData.class)
        .block();
  }
  /**
   Reads the GraphQL query from a file and returns it as a {@link String}.

   @return the GraphQL query as a {@link String}
   @throws IOException if an error occurs while reading the GraphQL query file
   */
  private String getGraphQlSchemaFromFileName() throws IOException {
    try (InputStream resourceStream = GraphQlService.class.getClassLoader()
            .getResourceAsStream(this.graphQlQueryFilepath)) {
      if (resourceStream == null) {
        log.error("Error accessing graphql resource stream from file at path: " + this.graphQlQueryFilepath);
        throw new RuntimeException("Error accessing graphql resource stream from file at path: " + this.graphQlQueryFilepath);
      }

      return new String(resourceStream.readAllBytes());
    } catch (IOException e) {
      log.error("Error reading graphql query file at path: " + this.graphQlQueryFilepath, e);
      throw e;
    }
  }
}
