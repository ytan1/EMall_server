package com.emall.service;

import com.emall.common.ServerResponse;
import com.emall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    public ServerResponse<String> addCategory(String categoryName, Integer parentId);
    public ServerResponse<String> setCategoryName(String categoryName, Integer categoryId);
    public ServerResponse<List<Category>> getParallelChildrenByParentId(Integer categoryId);
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
