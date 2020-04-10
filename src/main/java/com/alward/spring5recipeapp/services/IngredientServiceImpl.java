package com.alward.spring5recipeapp.services;

import com.alward.spring5recipeapp.commands.IngredientCommand;
import com.alward.spring5recipeapp.converters.IngredientToIngredientCommand;
import com.alward.spring5recipeapp.domain.Recipe;
import com.alward.spring5recipeapp.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class IngredientServiceImpl implements IngredientService {

    private final RecipeRepository recipeRepository;
    private final IngredientToIngredientCommand converter;

    public IngredientServiceImpl(RecipeRepository recipeRepository, IngredientToIngredientCommand converter) {
        this.recipeRepository = recipeRepository;
        this.converter = converter;
    }

    @Override
    public IngredientCommand findByRecipeIdAndByIngredientId(Long recipeId, Long ingredientId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        //TODO: Add proper error handling
        if (recipeOptional.isEmpty()) {
            log.error("Recipe with ID: " + recipeId + " not found");
        }

        Recipe recipe = recipeOptional.orElseThrow(() -> new RuntimeException("not found"));

        Optional<IngredientCommand> optionalIngredientCommand = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(converter::convert)
                .findFirst();

        //TODO: Add proper error handling
        if (optionalIngredientCommand.isEmpty()) {
            log.error("Ingredient with ID: " + ingredientId + " not found");
        }

        return optionalIngredientCommand.orElseThrow(() -> new RuntimeException("not found"));
    }
}
