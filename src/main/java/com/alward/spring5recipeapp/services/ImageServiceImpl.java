package com.alward.spring5recipeapp.services;

import com.alward.spring5recipeapp.domain.Recipe;
import com.alward.spring5recipeapp.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final RecipeRepository recipeRepository;

    public ImageServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    @Transactional
    public void saveImageFile(Long recipeId, MultipartFile file) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));

            byte[] fileBytes = file.getBytes();
            Byte[] bytes = new Byte[fileBytes.length];

            for (int i = 0; i < fileBytes.length; i++) {
                bytes[i] = fileBytes[i];
            }

            recipe.setImage(bytes);

            recipeRepository.save(recipe);

        } catch (IOException e) {
            //TODO: add proper error handling
            log.error("Error occurred while saving image", e);
            e.printStackTrace();
        }

    }
}
