package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

/**
 * search接口
 */
public interface ItemSearchService {
    /**
     * search方法,返回map
     * @param searchMap
     * @return
     */
    public Map search(Map searchMap);

    /**
     * 批量导入审核通过的sku
     * @param list
     */
    public void importList(List list);

    public void delete(List ids);
}
