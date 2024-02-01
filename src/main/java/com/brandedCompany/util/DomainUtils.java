package com.brandedCompany.util;


import com.brandedCompany.domain.*;
import com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition.OrderHistoryOrderStatus;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.exception.TableNotFoundException;
import com.brandedCompany.serivce.CRUDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.brandedCompany.util.DomainUtils.TABLE.*;


public class DomainUtils {

    private static final Logger logger = LoggerFactory.getLogger(DomainUtils.class);
     @Autowired private static CRUDService service;



    public enum NameLocation {
        FIRST, LAST, DEFAULT
    }


    public enum TABLE {
        CUSTOMERS, CONTACTS, COMMENTS, EMPLOYEES, BOARDS, ORDERS, ORDER_ITEMS,
        PRODUCTS, PRODUCT_CATEGORIES, INVENTORIES, WAREHOUSES, LOCATIONS,
        COUNTRIES, INVENTORY_DETAIL, ORDER_HISTORY, REGIONS,CARTS

    }




    private static TABLE convertConstant(Object rows) throws DomainNotFoundException, NullPointerException {
        Map<String, Object> mapRows = (Map<String, Object>) rows;
//        System.out.println("mapRows = " + mapRows);
        if (mapRows.containsKey("BOARD_ID") &&
                mapRows.containsKey("COMMENT_ID") &&
                mapRows.containsKey("EMPLOYEE_ID"))
            return TABLE.COMMENTS;

        else if ((mapRows.containsKey("ORDER_ID") &&
                mapRows.containsKey("ITEM_ID") &&
                mapRows.containsKey("PRODUCT_ID"))
            ||  mapRows.containsKey("itemId")
                && mapRows.containsKey("orderId")
                && mapRows.containsKey("productId")
                && mapRows.containsKey("quantity")
                && mapRows.containsKey("unitPrice"))
        return ORDER_ITEMS;

        else if ((mapRows.containsKey("CUSTOMER_ID") &&
                mapRows.containsKey("ORDER_ID"))||(mapRows.containsKey("customerId") &&
                mapRows.containsKey("status")))
            return ORDERS;
        else if (mapRows.containsKey("CONTACT_ID") &&
                mapRows.containsKey("CUSTOMER_ID"))
            return CONTACTS;


        else if (mapRows.containsKey("BOARD_ID") &&
                mapRows.containsKey("EMPLOYEE_ID"))
            return BOARDS;


        else if ((mapRows.containsKey("PRODUCT_ID") &&
                mapRows.containsKey("CATEGORY_ID")) ||
                (mapRows.containsKey("productId") &&
                        mapRows.containsKey("categoryId")))
            return PRODUCTS;


        else if (mapRows.containsKey("PRODUCT_ID") &&
                mapRows.containsKey("WAREHOUSE_ID"))
            return INVENTORIES;

        else if (mapRows.containsKey("WAREHOUSE_ID") &&
                mapRows.containsKey("LOCATION_ID"))
            return WAREHOUSES;

        else if (mapRows.containsKey("COUNTRY_ID") &&
                mapRows.containsKey("LOCATION_ID"))
            return LOCATIONS;
        else if (mapRows.containsKey("COUNTRY_ID") &&
                mapRows.containsKey("REGION_ID"))
            return COUNTRIES;
        else if (mapRows.containsKey("CUSTOMER_ID")&& mapRows.containsKey("PRODUCT_ID")) return CARTS;
        else if(mapRows.containsKey("customerId")&&mapRows.containsKey("productId")) return CARTS;
        else if (mapRows.containsKey("CUSTOMER_ID")) return CUSTOMERS;
        else if (mapRows.containsKey("CATEGORY_ID")) return PRODUCT_CATEGORIES;
        else if (mapRows.containsKey("REGION_ID")) return REGIONS;
        else if (mapRows.containsKey("EMPLOYEE_ID")) return EMPLOYEES;
        else if (mapRows.containsKey("CITY")) return INVENTORY_DETAIL;
        else if (mapRows.containsKey("ORDER_ID") && mapRows.containsKey("ITEM_ID")) return ORDER_HISTORY;
        throw new DomainNotFoundException("해당 도메인을 찾지 못했습니다.");
    }

    public static Domain packDomain(Object rows) throws Exception {

        Map<String, Object> row = (Map<String, Object>) rows;
//        System.out.println("rows = " + rows);
        TABLE table = convertConstant(rows);
//        System.out.println("table = " + table);
        switch (table) {

            case CONTACTS:
                return new Contact(
                        parseBigInt(row, "CONTACT_ID"),
                        parseStr(row, "FIRST_NAME"),
                        parseStr(row, "LAST_NAME"),
                        parseStr(row, "EMAIL"),
                        parseStr(row, "PHONE"),
                        parseBigInt(row, "CUSTOMER_ID"));

            case CUSTOMERS:
                return new Customer(
                        parseBigInt(row, "CUSTOMER_ID"),
                        parseStr(row, "NAME"),
                        parseStr(row, "ADDRESS"),
                        parseStr(row, "WEBSITE"),
                        parseDou(row, "CREDIT_LIMIT"));

            case BOARDS:
                BigInteger views = parseBigInt(row, "VIEWS");
                return new Board(
                        parseBigInt(row, "BOARD_ID"),
                        parseBigInt(row, "EMPLOYEE_ID"),
                        views.equals(BigInteger.ZERO) ? BigInteger.ZERO : views,
                        parseStr(row, "TITLE"),
                        parseStr(row, "CONTENT"),
                        parseStr(row, "PUBLISHER"),
                        parseLocalDateTime(row, "PUBLISHED_DATE"));

            case EMPLOYEES:
                BigInteger managerId = null;
                if (!isPresident(row)) managerId = parseBigInt(row, "MANAGER_ID");
                return new Employee(
                        parseBigInt(row, "EMPLOYEE_ID"),
                        managerId,
                        parseStr(row, "FIRST_NAME"),
                        parseStr(row, "LAST_NAME"),
                        parseStr(row, "EMAIL"),
                        parseStr(row, "PHONE"),
                        parseStr(row, "JOB_TITLE"),
                        parseLocalDate(row, "HIRE_DATE"));
            case COMMENTS:
                LocalDateTime modificationDate = null;
                if (null != row.get("MODIFICATION_DATE"))
                    modificationDate = parseLocalDateTime(row, "MODIFICATION_DATE");
                return new Comment(
                        parseBigInt(row, "COMMENT_ID"),
                        parseBigInt(row, "BOARD_ID"),
                        parseBigInt(row, "EMPLOYEE_ID"),
                        parseBigInt(row, "PARENT_COMMENT_ID"),
                        parseStr(row, "CONTENT"),
                        parseStr(row, "PUBLISHER"),
                        parseLocalDateTime(row, "REGISTRATION_DATE"),
                        modificationDate);
            case PRODUCTS:
                if (row.containsKey("PRODUCT_ID") &&
                        row.containsKey("CATEGORY_ID"))

                    return new Product(
                            parseBigInt(row, "PRODUCT_ID"),
                            parseBigInt(row, "CATEGORY_ID"),
                            parseStr(row, "PRODUCT_NAME"),
                            parseStr(row, "DESCRIPTION"),
                            parseDou(row, "STANDARD_COST"),
                            parseDou(row, "LIST_PRICE"));
                else if ((row.containsKey("productId") &&
                        row.containsKey("categoryId")))
                    return new Product(
                            parseBigInt(row, "productId"),
                            parseBigInt(row, "categoryId"),
                            parseStr(row, "productName"),
                            parseStr(row, "description"),
                            parseDou(row, "standardCost"),
                            parseDou(row, "listPrice"));
            case PRODUCT_CATEGORIES:
                return new ProductCategory(
                        parseBigInt(row, "CATEGORY_ID"),
                        parseStr(row, "CATEGORY_NAME"));
            case INVENTORIES:
                return new Inventory(
                        parseBigInt(row, "PRODUCT_ID"),
                        parseBigInt(row, "WAREHOUSE_ID"),
                        parseBigInt(row, "QUANTITY"));
            case REGIONS:
                return new Region(
                        parseBigInt(row, "REGION_ID")
                        , parseStr(row, "REGION_NAME"));
            case WAREHOUSES:
                return new Warehouse(
                        parseBigInt(row, "WAREHOUSE_ID"),
                        parseBigInt(row, "LOCATION_ID"),
                        parseStr(row, "WAREHOUSE_NAME"));
            case LOCATIONS:
                return new Location(
                        parseBigInt(row, "LOCATION_ID"),
                        parseBigInt(row, "COUNTRY_ID"),
                        parseStr(row, "ADDRESS"),
                        parseStr(row, "POSTAL_CODE"),
                        parseStr(row, "CITY"),
                        parseStr(row, "STATE"));
            case COUNTRIES:
                return new Country(
                        parseBigInt(row, "COUNTRY_ID"),
                        parseBigInt(row, "REGION_ID"),
                        parseStr(row, "COUNTRY_NAME"));
            case ORDERS:
                if(row.containsKey("ORDER_ID")&& row.containsKey("CUSTOMER_ID") &&
                row.containsKey("STATUS") && row.containsKey("ORDER_DATE"))
                    return new Order(parseBigInt(row, "ORDER_ID"),
                                     parseBigInt(row, "CUSTOMER_ID"),
                                     parseBigInt(row, "SALESMAN_ID"),
                                     parseStr(row, "STATUS"),
                                     parseLocalDate(row, "ORDER_DATE"));
                else if(row.containsKey("orderId")&&row.containsKey("customerId")&&
                        row.containsKey("status"))
                    return new Order(
                            parseBigInt(row,"orderId"),
                            parseBigInt(row,"customerId"),
                            parseStr(row,"status"));
            case ORDER_ITEMS:
                if(row.containsKey("itemId")
                        && row.containsKey("productId")
                        && row.containsKey("quantity")
                        && row.containsKey("unitPrice"))
                    return new OrderItem(
                            parseBigInt(row,"orderId"),
                            parseBigInt(row,"itemId"),
                            parseBigInt(row,"productId"),
                            parseDou(row,"quantity"),
                            parseDou(row,"unitPrice"));
                else if(row.containsKey("ORDER_ID")&& row.containsKey("ITEM_ID")
                && row.containsKey("PRODUCT_ID"))
                return new OrderItem(
                        parseBigInt(row, "ORDER_ID"),
                        parseBigInt(row, "ITEM_ID"),
                        parseBigInt(row, "PRODUCT_ID"),
                        parseDou(row, "QUANTITY"),
                        parseDou(row, "UNIT_PRICE"));
            case ORDER_HISTORY:
                return new OrderHistory(parseBigInt(row, "ORDER_ID")
                        , parseBigInt(row, "ITEM_ID")
                        ,parseStr(row,"PRODUCT_NAME")
                        , parseStr(row, "ORDERER")
                        , parseStr(row, "ADDRESS")
                        , parseStr(row, "SALESMAN_NAME")
                        , parseOrderHistoryOrderStatus(row, "ORDER_STATUS")
                        , parseLocalDate(row, "ORDER_DATE")
                        , parseBigInt(row, "QUANTITY")
                        , parseDou(row, "UNIT_PRICE")
                        , parseDou(row, "TOTAL_PRICE")
                );
            case INVENTORY_DETAIL:
                return new InventoryDetail(
                        parseStr(row, "REGION_NAME")
                        , parseStr(row, "COUNTRY_NAME")
                        , parseStr(row, "CITY")
                        , parseStr(row, "ADDRESS")
                        , parseStr(row, "WAREHOUSE_NAME")
                        , parseStr(row, "PRODUCT_NAME")
                        , parseBigInt(row, "QUANTITY")
                        , parseDou(row, "LIST_PRICE")
                );
            case CARTS:
                if(row.containsKey("CUSTOMER_ID"))
                return new Cart(
                        parseBigInt(row,"CUSTOMER_ID"),
                        parseBigInt(row,"PRODUCT_ID"),
                        parseBigInt(row,"QUANTITY"),
                        parseStr(row,"PRODUCT_NAME"),
                        parseDou(row,"UNIT_PRICE"),
                        parseLocalDateTime(row,"INCLUDED_DATE"));
                else return new Cart(parseBigInt(row,"customerId"),
                        parseBigInt(row,"productId"),
                        parseBigInt(row,"quantity"),
                        parseStr(row,"productName"),
                        parseDou(row,"unitPrice"),
                        parseLocalDateTime(row,"INCLUDED_DATE"));
            default:
                throw new DomainNotFoundException(rows.toString());

        }


    }

    public static BigInteger parseBigInt(Map<String, Object> row, String rowName) {

//        try {
//            BigInteger.valueOf(Long.valueOf(String.valueOf(row.get(rowName))));
//        } catch (NumberFormatException e) {
//            System.out.println("row = " + row);
//            System.out.println("rowName = " + rowName);
//            System.out.println(String.valueOf(row.get(rowName)));
//            e.printStackTrace();
//        }
        Object rowVal = row.get(rowName);
        if (null == rowVal) return null;
        return BigInteger.valueOf(Long.valueOf(String.valueOf(rowVal)));


    }

    private static String parseStr(Map<String, Object> row, String rowName) {

        return String.valueOf(row.get(rowName));
    }

    private static Double parseDou(Map<String, Object> row, String rowName) {
//        if (rowName.equals("CREDIT_LIMIT")) {
//            System.out.println("rowName = " + rowName);
//            System.out.println("row = " + row);
//        }
        return Double.valueOf(String.valueOf(row.get(rowName)));
    }

    private static LocalDateTime parseLocalDateTime(Map<String, Object> row, String rowName) {
        return Timestamp.valueOf(String.valueOf(row.get(rowName))).toLocalDateTime();
    }
    private static LocalDate parseLocalDate2(Map<String,Object > row,String rowName)
    {
        return LocalDate.parse(String.valueOf(row.get(rowName)), DateTimeFormatter.ISO_DATE);
    }
    private static LocalDate parseLocalDate(Map<String, Object> row, String rowName) {
        return Timestamp.valueOf(String.valueOf(row.get(rowName))).toLocalDateTime().toLocalDate();
    }


    private static OrderHistoryOrderStatus parseOrderHistoryOrderStatus(Map<String, Object> row, String rowName) {
        Optional<OrderHistoryOrderStatus> orderHistoryOrderStatus = Arrays.stream(OrderHistoryOrderStatus.values()).filter(status -> status.name().equals(row.get(rowName))).findAny();
        return orderHistoryOrderStatus.orElse(null);

    }

    private static boolean isPresident(Map<String, Object> row) {
        return BigInteger.valueOf(Long.valueOf(String.valueOf(row.get("EMPLOYEE_ID")))).equals(BigInteger.ONE);
    }

    public static String getFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }


    public static Class<? extends Domain> getType(TABLE table) throws ClassNotFoundException {
        switch (table) {

            case BOARDS:
                return (Class<? extends Domain>) new Board().getClass();

            case COMMENTS:
                return (Class<? extends Domain>) new Comment().getClass();

            case CONTACTS:
                return (Class<? extends Domain>) new Contact().getClass();

            case CUSTOMERS:
                return (Class<? extends Domain>) new Customer().getClass();
            case EMPLOYEES:
                return (Class<? extends Domain>) new Employee().getClass();

            case ORDERS:
                return (Class<? extends Domain>) new Order().getClass();
            case ORDER_ITEMS:
                return (Class<? extends Domain>) new OrderItem().getClass();
            case PRODUCTS:
                return (Class<? extends Domain>) new Product().getClass();
            case PRODUCT_CATEGORIES:
                return (Class<? extends Domain>) new ProductCategory().getClass();
            case INVENTORIES:
                return (Class<? extends Domain>) new Inventory().getClass();
            case WAREHOUSES:
                return (Class<? extends Domain>) new Warehouse().getClass();
            case LOCATIONS:
                return (Class<? extends Domain>) new com.brandedCompany.domain.Location().getClass();
            case COUNTRIES:
                return (Class<? extends Domain>) new Country().getClass();
            case REGIONS:
                return (Class<? extends Domain>) new Region().getClass();
            case CARTS:
                return (Class<? extends Domain>) new Cart().getClass();

            default:
                logger.error("table not found", TableNotFoundException.class);
                throw new TableNotFoundException(table);

        }

    }

    public static Map<String, Object> getMap() {
        return new HashMap();
    }

    public static Map mapping(Domain domain, Map map) {
        map.put("clazz", domain.getClass().getSimpleName());
        map.put("domain", domain);
        return map;
    }

    public static Map mapping(Class<? extends Domain> clazz, Map map) {
        map.put("clazz", clazz.getSimpleName());
        return map;
    }


    public static Map mapping(Collection<? extends Domain> domains, Map map) {
        map.put("domains", domains);
        return map;
    }
}