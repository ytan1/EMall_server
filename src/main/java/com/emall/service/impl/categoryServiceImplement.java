package com.emall.service.impl;

import com.emall.common.ServerResponse;
import com.emall.dao.CategoryMapper;
import com.emall.pojo.Category;
import com.emall.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class categoryServiceImplement implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse<String> addCategory(String categoryName, Integer parentId){
        if(StringUtils.isBlank(categoryName) || parentId == null){
            return ServerResponse.responseByError("Parameter exception.");
        }

        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory.setParentId(parentId);
        int resultCount = categoryMapper.insertSelective(newCategory);
        if(resultCount == 0){
            return ServerResponse.responseByError("Insert fails.");
        }
        return ServerResponse.responseBySuccessMessage("Insert success.");
    }

    public ServerResponse<String> setCategoryName(String categoryName, Integer categoryId){
        if(StringUtils.isBlank(categoryName) || categoryId == null){
            return ServerResponse.responseByError("Parameter exception.");
        }

        Category newCategory = new Category();
        newCategory.setId(categoryId);
        newCategory.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(newCategory);
        if(resultCount == 0){
            return ServerResponse.responseByError("Update name fails.");
        }
        return ServerResponse.responseBySuccessMessage("Update name success.");
    }

    public ServerResponse<List<Category>> getParallelChildrenByParentId(Integer categoryId){
        List<Category> categoryList;
        categoryList = categoryMapper.getParallelChildrenByParentId(categoryId);

        //logger if List is null

        return ServerResponse.responseBySuccess("Get children category success.", categoryList);
    }

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        List<Integer> idList = Lists.newArrayList();

        Category parentCategory = categoryMapper.selectByPrimaryKey(categoryId);
        if(parentCategory!=null) {
            categorySet.add(parentCategory); //can't categorySet.add(null), have to use if condition!!!!
        }
        this.addChildrenByCategory(categorySet, categoryId);



        for(Category setItem: categorySet){
            idList.add(setItem.getId());
        }

        return ServerResponse.responseBySuccess("Select ids susscess.", idList);


    }

    private Set<Category> addChildrenByCategory(Set<Category> categorySet, Integer parentCategoryId){
        List<Category> childCategory = categoryMapper.getParallelChildrenByParentId(parentCategoryId);

        if(childCategory != null){
            for(Category listItem: childCategory){
                categorySet.add(listItem);
                addChildrenByCategory(categorySet, listItem.getId());
            }
        }
        return categorySet;
    }



}
