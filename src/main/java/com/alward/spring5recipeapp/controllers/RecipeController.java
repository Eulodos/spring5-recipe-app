package com.alward.spring5recipeapp.controllers;

import com.alward.spring5recipeapp.commands.RecipeCommand;
import com.alward.spring5recipeapp.exceptions.NotFoundException;
import com.alward.spring5recipeapp.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@Slf4j
public class RecipeController {

    private static final String RECIPE_RECIPEFORM = "recipe/recipeform";
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findById(Long.valueOf(id)));
        return "recipe/show";
    }

    @GetMapping("/recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());
        return RECIPE_RECIPEFORM;
    }


    @PostMapping("/recipe")
    public String saveOrUpdateRecipe(@Valid @ModelAttribute("recipe") RecipeCommand recipeCommand, BindingResult result) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return RECIPE_RECIPEFORM;
        }
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(recipeCommand);

        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @GetMapping("/recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(id)));
        return RECIPE_RECIPEFORM;
    }

    @GetMapping("/recipe/{id}/delete")
    public String deleteRecipe(@PathVariable String id) {
        log.debug("Id of the deleted recipe is: " + id);

        recipeService.deleteById(Long.valueOf(id));
        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(NotFoundException exception) {
        log.error("Handling NotFoundException");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error404");
        modelAndView.addObject("exception", exception);
        return modelAndView;
    }
}
