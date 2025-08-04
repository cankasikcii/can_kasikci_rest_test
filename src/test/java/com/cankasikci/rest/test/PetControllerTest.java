package com.cankasikci.rest.test;

import org.junit.jupiter.api.Test;

import static com.cankasikci.rest.test.TestConfig.waitStep;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PetControllerTest {

  @Test
  public void shouldCreateFindUpdateAndDeletePetById() {

    // 1. Create a new pet with specified ID and details
    String createBody = """
        {
          "id": %d,
          "category": {"id": 888, "name": "dog"},
          "name": "kimyon",
          "photoUrls": ["https://example.com/photo.jpg"],
          "tags": [{"id": 888, "name": "kimyon"}],
          "status": "available"
        }
        """.formatted(TestConfig.CREATE_PET_ID);

    // Send POST request to create the pet and verify the name
    given()
        .baseUri(TestConfig.BASE_URI)
        .header("Content-Type", "application/json")
        .body(createBody)
        .when()
        .post("/pet")
        .then()
        .statusCode(200)
        .body("name", equalTo("kimyon"));

    waitStep();

    // 3. Update the pet's category name and status (simulate PUT update)
    String updateBody = """
        {
          "id": %d,
          "category": {"id": 888, "name": "newCategoryName"},
          "name": "kimyon",
          "photoUrls": ["https://example.com/photo.jpg"],
          "tags": [{"id": 888, "name": "kimyon"}],
          "status": "sold"
        }
        """.formatted(TestConfig.CREATE_PET_ID);

    waitStep();

    // Send PUT request to update the pet and verify updated values
    given()
        .baseUri(TestConfig.BASE_URI)
        .header("Content-Type", "application/json")
        .body(updateBody)
        .when()
        .put("/pet")
        .then()
        .statusCode(200)
        .body("id", equalTo(TestConfig.CREATE_PET_ID))
        .body("category.name", equalTo("newCategoryName"))
        .body("status", equalTo("sold"));

    waitStep();

    // 4. Delete the pet by ID
    given()
        .baseUri(TestConfig.BASE_URI)
        .when()
        .delete("/pet/{petId}", TestConfig.CREATE_PET_ID)
        .then()
        .statusCode(200)
        .body("code", equalTo(200))
        .body("message", equalTo(String.valueOf(TestConfig.CREATE_PET_ID)));
  }

  @Test
  public void shouldUpdatePet() {
    // Prepare body for updating an existing pet
    String updateBody = """
        {
          "id": %d,
          "category": {"id": 9, "name": "Köpek"},
          "name": "Karabas",
          "photoUrls": ["https://picsum.photos/101/753"],
          "tags": [{"id": 813, "name": "Sadık"}],
          "status": "available"
        }
        """.formatted(TestConfig.UPDATE_PET_ID);

    // 1. Send PUT request to update the pet
    given()
        .baseUri(TestConfig.BASE_URI)
        .header("Content-Type", "application/json")
        .body(updateBody)
        .when()
        .put("/pet")
        .then()
        .statusCode(200)
        .body("id", equalTo(TestConfig.UPDATE_PET_ID))
        .body("name", equalTo("Karabas"))
        .body("status", equalTo("available"))
        .body("category.name", equalTo("Köpek"))
        .body("tags[0].name", equalTo("Sadık"));

    waitStep();

    // 2. Get the updated pet and verify all values
    given()
        .baseUri(TestConfig.BASE_URI)
        .when()
        .get("/pet/" + TestConfig.UPDATE_PET_ID)
        .then()
        .statusCode(200)
        .body("id", equalTo(TestConfig.UPDATE_PET_ID))
        .body("name", equalTo("Karabas"))
        .body("status", equalTo("available"))
        .body("category.name", equalTo("Köpek"))
        .body("tags[0].name", equalTo("Sadık"));

    // 3. Check if the updated pet is listed under status=available
    given()
        .baseUri(TestConfig.BASE_URI)
        .queryParam("status", "available")
        .when()
        .get("/pet/findByStatus")
        .then()
        .statusCode(200)
        .body("findAll { it.id == %d }.size()".formatted(TestConfig.UPDATE_PET_ID), equalTo(1));

    given()
        .baseUri(TestConfig.BASE_URI)
        .when()
        .get("/pet/" + TestConfig.UPDATE_PET_ID)
        .then()
        .statusCode(200)
        .body("id", equalTo(TestConfig.UPDATE_PET_ID))
        .body("name", equalTo("Karabas"))
        .body("status", equalTo("available"))
        .body("category.name", equalTo("Köpek"))
        .body("tags[0].name", equalTo("Sadık"));
  }

  @Test
  public void shouldReturn404WhenGettingNonExistingPet() {
    // Try to get a non-existent pet and expect a 404 Not Found
    given()
        .baseUri(TestConfig.BASE_URI)
        .when()
        .get("/pet/" + TestConfig.NON_EXISTING_PET_ID)
        .then()
        .statusCode(404);
  }

  @Test
  public void shouldReturn404WhenDeletingNonExistingPet() {
    // Try to delete a non-existent pet and expect a 404 Not Found
    given()
        .baseUri(TestConfig.BASE_URI)
        .when()
        .delete("/pet/{petId}", TestConfig.NON_EXISTING_PET_ID)
        .then()
        .statusCode(404);
  }
}
