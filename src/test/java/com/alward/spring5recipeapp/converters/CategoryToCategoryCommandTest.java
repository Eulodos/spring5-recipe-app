package com.alward.spring5recipeapp.converters;

import com.alward.spring5recipeapp.commands.CategoryCommand;
import com.alward.spring5recipeapp.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryToCategoryCommandTest {

    public static final Long ID_VALUE = 1L;
    public static final String DESCRIPTION = "descript";
    CategoryToCategoryCommand convter;

    @BeforeEach
    public void setUp()  {
        convter = new CategoryToCategoryCommand();
    }

    @Test
    public void testNullObject()  {
        assertNull(convter.convert(null));
    }

    @Test
    public void testEmptyObject()  {
        assertNotNull(convter.convert(new Category()));
    }

    @Test
    public void convert()  {
        //given
        Category category = new Category();
        category.setId(ID_VALUE);
        category.setDescription(DESCRIPTION);

        //when
        CategoryCommand categoryCommand = convter.convert(category);

        //then
        assertNotNull(categoryCommand);
        assertEquals(ID_VALUE, categoryCommand.getId());
        assertEquals(DESCRIPTION, categoryCommand.getDescription());

    }

}