package com.alward.spring5recipeapp.controllers;

import com.alward.spring5recipeapp.commands.RecipeCommand;
import com.alward.spring5recipeapp.services.ImageService;
import com.alward.spring5recipeapp.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @Mock
    private RecipeService recipeService;

    private MockMvc mockMvc;

    private ImageController imageController;

    @BeforeEach
    void setUp() {
        imageController = new ImageController(imageService, recipeService);
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }


    @Test
    void testGetImageForm() throws Exception {
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);

        mockMvc.perform(get("/recipe/1/image"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"));

        verify(recipeService, times(1)).findCommandById(anyLong());
    }

    @Test
    void testHandleImageUpload() throws Exception {
        MockMultipartFile imagefile = new MockMultipartFile("imagefile", "testing.txt", "text/plain",
                "Some random words which don't make any sense".getBytes());

        mockMvc.perform(multipart("/recipe/1/image").file(imagefile))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/recipe/1/show"));

        verify(imageService, times(1)).saveImageFile(anyLong(), any());
    }

    @Test
    void testRenderImageFromDB() throws Exception {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        String s = "Fake data as substitution for a real image";
        byte[] primitiveBytes = s.getBytes();
        Byte[] bytes = new Byte[primitiveBytes.length];

        for (int i = 0; i < primitiveBytes.length; i++) {
            bytes[i] = primitiveBytes[i];
        }

        recipeCommand.setImage(bytes);

        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);

        //when

        MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/recipeimage"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        byte[] responseContentAsByteArray = response.getContentAsByteArray();

        //then
        assertEquals(primitiveBytes.length, responseContentAsByteArray.length);
    }
}