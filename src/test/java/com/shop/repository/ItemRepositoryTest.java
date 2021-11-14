package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Saving Item")
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("Test Item");
        item.setPrice(10000);
        item.setItemDetail("Item Detail");
        item.setItemSellStaus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createItemList() {
        for (int i = 0; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("Test Item_" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("Item Detail_" + i);
            item.setItemSellStaus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("Finding Items")
    public void findByItemNmTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("Test Item_1");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Or case for ItemNm, ItemDetail")
    public void findByItemNmOrItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("Test Item_1", "Item Detail_5");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("LessThen case for Price")
    public void findByPriceLessThenTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10010);
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("OrderByDesc case for Price")
    public void findByPriceLessThanOrderByPriceDescTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10010);
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query case for itemDetail")
    public void findByItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("1");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery case by itemDetail")
    public void findByItemDetailByNative() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("_");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl case for searching_1")
    public void queryDslTest() {
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStaus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "_1" + "%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){
        for (int i = 0; i <= 5; i++) {
            Item item = new Item();
            item.setItemNm("Test Item_" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("Item Detail_" + i);
            item.setItemSellStaus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for (int i = 6; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("Test Item_" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("Item Detail_" + i);
            item.setItemSellStaus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("QuerydslPredicateExecutor case for searching_2")
    public void queryDslPredicateExecutorTest(){
        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QItem item = QItem.item;

        String itemDetail = "Item Detail";
        int price = 10003;
        String itemSellStatus = "SELL";

        booleanBuilder.and(item.itemDetail.like("%"+ itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));

        if(StringUtils.equals(itemSellStatus, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStaus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0,5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("Total elements : " + itemPagingResult.getTotalElements());

        List<Item> pagingItemList = itemPagingResult.getContent();
        for(Item pagingItem : pagingItemList){
            System.out.println(pagingItem.toString());
        }
    }
}