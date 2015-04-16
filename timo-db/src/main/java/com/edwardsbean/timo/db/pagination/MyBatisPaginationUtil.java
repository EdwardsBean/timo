package com.edwardsbean.timo.db.pagination;

import com.edwardsbean.timo.common.pagination.PageList;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * MyBatis的分页查询辅助工具，适用于百xx修改版的MyBatis生成工具生成的代码
 *
 * TODO 需要处理WithBLOBs的情况
 *
 */
public class MyBatisPaginationUtil {
    private static final String COUNT_METHOD_NAME  = "countByExample";
    private static final String SELECT_METHOD_NAME = "selectByExampleWithPaging";

    /**
     * 根据条件执行分页查询，如果分页信息不正确，默认用第0条开始，每页10条进行查询
     *
     * @param mapper 具体的映射实现类
     * @param example 查询的条件，其中包含需要查询的分页信息startIndex和pageSize
     * @param <T> 返回实体对象类型
     * @return 带有一页数据和分页信息的PageList
     */
    public static <T> PageList<T> selectByExample(Object mapper, Object example) {
        Assert.notNull(mapper);
        Assert.notNull(example);

        int itemOffset = getOrSetDefaultValue(example, "startIndex", 0);
        int itemsPerPage = getOrSetDefaultValue(example, "pageSize", 10);

        int total = getTotalCount(mapper, example);
        List<T> list = getCurrentList(mapper, example);

        PageList<T> pageList = new PageList<T>(list);
        pageList.getPaginator().setItems(total);
        pageList.getPaginator().setItemsPerPage(itemsPerPage);
        pageList.getPaginator().setItem(itemOffset);

        return pageList;
    }

    private static int getOrSetDefaultValue(Object example, String fieldName, int defaultValue) {
        Field field = ReflectionUtils.findField(example.getClass(), fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Example object has no " + fieldName + "!");
        }
        ReflectionUtils.makeAccessible(field);
        int value = (Integer) ReflectionUtils.getField(field, example);
        if (value < 0) {
            ReflectionUtils.setField(field, example, defaultValue);
            value = defaultValue;
        }
        return value;
    }

    private static int getTotalCount(Object mapper, Object example) {
        Method method = findMethod(mapper, example, COUNT_METHOD_NAME);
        return (Integer) ReflectionUtils.invokeMethod(method, mapper, example);
    }

    private static <T> List<T> getCurrentList(Object mapper, Object example) {
        Method method = findMethod(mapper, example, SELECT_METHOD_NAME);
        Object results = ReflectionUtils.invokeMethod(method, mapper, example);
        if (results instanceof List) {
            return (List<T>) results;
        } else {
            throw new IllegalArgumentException("The result of " + SELECT_METHOD_NAME + "(" + example
                    .getClass().getSimpleName() + ") is NOT List");
        }
    }

    private static Method findMethod(Object mapper, Object example, String name) {
        Method method = ReflectionUtils.findMethod(mapper.getClass(), name, example.getClass());
        if (method == null) {
            throw new IllegalArgumentException("Can not find " + name + "(" + example.getClass()
                    .getSimpleName() + ") in the given Mapper!");
        }
        return method;
    }
}