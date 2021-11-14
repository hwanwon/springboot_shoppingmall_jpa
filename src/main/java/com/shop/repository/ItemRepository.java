package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor {

    List<Item> findByItemNm(String itemNm);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    List<Item> findByPriceLessThan(Integer price);

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    /*
     * @Query Annotation 은 복잡한 쿼리를 작성 할 때 사용한다.
     * 쿼리를 문자열로 입력하기 때문에 컴파일 시에 에러를 발견하지 못한다.
     * */
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    /*
     * Native Query 를 사용 할 때는  value 에 쿼리를 작성하고 사용한다. native Query = true 설정을 한다.
     * */
    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc"
            , nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}
