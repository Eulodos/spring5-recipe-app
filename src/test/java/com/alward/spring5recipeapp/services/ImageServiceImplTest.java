package com.alward.spring5recipeapp.services;

import com.alward.spring5recipeapp.domain.Recipe;
import com.alward.spring5recipeapp.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    RecipeRepository recipeRepository;

    ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl(recipeRepository);
    }

    @Test
    void testSaveImageFile() throws IOException {
        //given
        MultipartFile imagefile = new MockMultipartFile("imagefile", "testing.txt",
                "text/plain", "Some random words which don't make any sense".getBytes());

        Recipe recipe = new Recipe();
        long recipeId = 1L;
        recipe.setId(recipeId);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        ArgumentCaptor<Recipe> recipeArgumentCaptor = ArgumentCaptor.forClass(Recipe.class);

        //when
        imageService.saveImageFile(recipeId, imagefile);

        //then
        verify(recipeRepository, times(1)).save(recipeArgumentCaptor.capture());
        Recipe savedRecipe = recipeArgumentCaptor.getValue();
        assertEquals(imagefile.getBytes().length, savedRecipe.getImage().length);
    }
}