package com.alward.spring5recipeapp.services;

import com.alward.spring5recipeapp.commands.IngredientCommand;
import com.alward.spring5recipeapp.converters.IngredientCommandToIngredient;
import com.alward.spring5recipeapp.converters.IngredientToIngredientCommand;
import com.alward.spring5recipeapp.domain.Ingredient;
import com.alward.spring5recipeapp.domain.Recipe;
import com.alward.spring5recipeapp.repositories.RecipeRepository;
import com.alward.spring5recipeapp.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class IngredientServiceImpl implements IngredientService {

    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;

    public IngredientServiceImpl(RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository, IngredientToIngredientCommand ingredientToIngredientCommand, IngredientCommandToIngredient ingredientCommandToIngredient) {
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
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
                .map(ingredientToIngredientCommand::convert)
                .findFirst();

        //TODO: Add proper error handling
        if (optionalIngredientCommand.isEmpty()) {
            log.error("Ingredient with ID: " + ingredientId + " not found");
        }

        return optionalIngredientCommand.orElseThrow(() -> new RuntimeException("not found"));
    }

    @Override
    @Transactional
    public IngredientCommand save(IngredientCommand ingredientCommand) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(ingredientCommand.getRecipeId());

        if (recipeOptional.isEmpty()) {
            //TODO: add proper error handling
            log.error("Recipe not found for Id: " + ingredientCommand.getRecipeId());
            return new IngredientCommand();
        } else {
            Recipe recipe = recipeOptional.get();

            Optional<Ingredient> optionalIngredient = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
                    .findFirst();

            if (optionalIngredient.isPresent()) {
                Ingredient ingredientToBeUpdated = optionalIngredient.get();
                ingredientToBeUpdated.setAmount(ingredientCommand.getAmount());
                ingredientToBeUpdated.setDescription(ingredientCommand.getDescription());
                ingredientToBeUpdated.setUnitOfMeasure(unitOfMeasureRepository
                        .findById(ingredientCommand.getUnitOfMeasure().getId())
                        .orElseThrow(() -> new RuntimeException("Unit of Measure not found"))); //TODO: add proper error handling
            } else {
                Ingredient ingredient = ingredientCommandToIngredient.convert(ingredientCommand);
                ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }

            Recipe savedRecipe = recipeRepository.save(recipe);

            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
                    .findFirst();

            if (savedIngredientOptional.isEmpty()) {
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredient -> recipeIngredient.getDescription().equals(ingredientCommand.getDescription()))
                        .filter(recipeIngredient -> recipeIngredient.getAmount().equals(ingredientCommand.getAmount()))
                        .filter(recipeIngredient -> recipeIngredient.getUnitOfMeasure().getId().equals(ingredientCommand.getUnitOfMeasure().getId()))
                        .findFirst();
            }
            return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
        }
    }

    @Override
    public void deleteById(Long recipeId, Long ingredientId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        if (recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            log.debug("Recipe found - ID: " + recipe.getId());

            Optional<Ingredient> optionalIngredient = recipe.getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientId))
                    .findFirst();

            if (optionalIngredient.isPresent()) {
                Ingredient ingredient = optionalIngredient.get();
                log.debug("Ingredient found - ID: " + ingredient.getId());

                ingredient.setRecipe(null);
                recipe.getIngredients().remove(ingredient);
                recipeRepository.save(recipe);
            }
        } else {
            log.debug("Recipe not found - searched ID " + recipeId);
        }
    }
}
