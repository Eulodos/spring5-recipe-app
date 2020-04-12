package com.alward.spring5recipeapp.services;

import com.alward.spring5recipeapp.commands.IngredientCommand;
import com.alward.spring5recipeapp.converters.IngredientCommandToIngredient;
import com.alward.spring5recipeapp.converters.IngredientToIngredientCommand;
import com.alward.spring5recipeapp.converters.UnitOfMeasureCommandToUnitOfMeasure;
import com.alward.spring5recipeapp.converters.UnitOfMeasureToUnitOfMeasureCommand;
import com.alward.spring5recipeapp.domain.Ingredient;
import com.alward.spring5recipeapp.domain.Recipe;
import com.alward.spring5recipeapp.repositories.RecipeRepository;
import com.alward.spring5recipeapp.repositories.UnitOfMeasureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    UnitOfMeasureRepository unitOfMeasureRepository;

    private IngredientCommandToIngredient ingredientCommandToIngredient;

    private IngredientToIngredientCommand ingredientToIngredientCommand;

    private IngredientService ingredientService;

    @BeforeEach
    void setUp() {
        ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
        ingredientService = new IngredientServiceImpl(recipeRepository, unitOfMeasureRepository, ingredientToIngredientCommand, ingredientCommandToIngredient);
    }

    @Test
    void testFindByRecipeIdAndByIngredientId() {
        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);
        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId(3L);

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);

        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);
        //when
        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndByIngredientId(1L, 3L);

        //then
        assertEquals(3L, ingredientCommand.getId());
        assertEquals(1L, ingredientCommand.getRecipeId());
        verify(recipeRepository, times(1)).findById(anyLong());

    }

    @Test
    void testSaveRecipeCommand() {
        //given
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(4L);
        ingredientCommand.setRecipeId(2L);

        Optional<Recipe> optionalRecipe = Optional.of(new Recipe());

        Recipe savedRecipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(4L);
        savedRecipe.addIngredient(ingredient);

        when(recipeRepository.findById(anyLong())).thenReturn(optionalRecipe);
        when(recipeRepository.save(any())).thenReturn(savedRecipe);

        //when
        IngredientCommand savedCommand = ingredientService.save(ingredientCommand);

        //then
        assertEquals(4L, savedCommand.getId());
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void testDeleteById() {
        //given
        Recipe recipe = new Recipe();
        recipe.setId(4L);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(22L);

        ingredient.setRecipe(recipe);
        recipe.addIngredient(ingredient);
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        //when
        ingredientService.deleteById(4L, 22L);

        //then
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }
}