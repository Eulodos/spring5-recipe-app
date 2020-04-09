package com.alward.spring5recipeapp.converters;

import com.alward.spring5recipeapp.commands.CategoryCommand;
import com.alward.spring5recipeapp.commands.IngredientCommand;
import com.alward.spring5recipeapp.commands.NotesCommand;
import com.alward.spring5recipeapp.commands.RecipeCommand;
import com.alward.spring5recipeapp.domain.Difficulty;
import com.alward.spring5recipeapp.domain.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeCommandToRecipeTest {

    private static final Long RECIPE_ID = 1L;
    private static final Integer COOK_TIME = Integer.valueOf("5");
    private static final Integer PREP_TIME = Integer.valueOf("7");
    private static final String DESCRIPTION = "My Recipe";
    private static final String DIRECTIONS = "Directions";
    private static final Difficulty DIFFICULTY = Difficulty.EASY;
    private static final Integer SERVINGS = Integer.valueOf("3");
    private static final String SOURCE = "Source";
    private static final String URL = "Some URL";
    private static final Long CAT_ID_1 = 1L;
    private static final Long CAT_ID2 = 2L;
    private static final Long INGRED_ID_1 = 3L;
    private static final Long INGRED_ID_2 = 4L;
    private static final Long NOTES_ID = 9L;

    private RecipeCommandToRecipe recipeConverter;

    @BeforeEach
    void setUp() {
        recipeConverter = new RecipeCommandToRecipe(
                new CategoryCommandToCategory(),
                new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure()),
                new NotesCommandToNotes());
    }

    @Test
    void convertNull() {
        assertNull(recipeConverter.convert(null));
    }

    @Test
    void convertEmptyObject() {
        assertNotNull(recipeConverter.convert(new RecipeCommand()));
    }

    @Test
    void convertTest() {
        //given
        RecipeCommand command = new RecipeCommand();
        command.setId(RECIPE_ID);
        command.setDescription(DESCRIPTION);

        NotesCommand notesCommand = new NotesCommand();
        notesCommand.setId(NOTES_ID);
        command.setNotes(notesCommand);

        command.setUrl(URL);
        command.setSource(SOURCE);
        command.setServings(SERVINGS);
        command.setPrepTime(PREP_TIME);
        command.setDirections(DIRECTIONS);
        command.setDifficulty(DIFFICULTY);
        command.setCookTime(COOK_TIME);

        CategoryCommand category = new CategoryCommand();
        category.setId(CAT_ID_1);

        CategoryCommand category2 = new CategoryCommand();
        category2.setId(CAT_ID2);

        command.getCategories().add(category);
        command.getCategories().add(category2);

        IngredientCommand ingredient = new IngredientCommand();
        ingredient.setId(INGRED_ID_1);

        IngredientCommand ingredient2 = new IngredientCommand();
        ingredient2.setId(INGRED_ID_2);

        command.getIngredients().add(ingredient);
        command.getIngredients().add(ingredient2);

        //when
        Recipe convertedRecipe = recipeConverter.convert(command);

        //then
        assertNotNull(convertedRecipe);
        assertEquals(RECIPE_ID, convertedRecipe.getId());
        assertEquals(DESCRIPTION, convertedRecipe.getDescription());
        assertEquals(NOTES_ID, convertedRecipe.getNotes().getId());
        assertEquals(URL, convertedRecipe.getUrl());
        assertEquals(SOURCE, convertedRecipe.getSource());
        assertEquals(SERVINGS, convertedRecipe.getServings());
        assertEquals(PREP_TIME, convertedRecipe.getPrepTime());
        assertEquals(DIRECTIONS, convertedRecipe.getDirections());
        assertEquals(DIFFICULTY, convertedRecipe.getDifficulty());
        assertEquals(COOK_TIME, convertedRecipe.getCookTime());
        assertEquals(2, convertedRecipe.getCategories().size());
        assertEquals(2, convertedRecipe.getIngredients().size());

    }
}