package com.alward.spring5recipeapp.services;

import com.alward.spring5recipeapp.commands.IngredientCommand;

public interface IngredientService {

    IngredientCommand findByRecipeIdAndByIngredientId(Long recipeId, Long ingredientId);

    IngredientCommand save(IngredientCommand ingredientCommand);

    void deleteById(Long recipeId, Long ingredientId);
}
