package com.brandedCompany.serivce;

import com.brandedCompany.domain.*;
import com.brandedCompany.util.DomainUtils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static com.brandedCompany.util.DomainUtils.TABLE.*;
import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql",
        "classpath:schema/brandedCompany_data.sql"})
@DisplayName("해당 테이블 컬럼 테스트")
public class CRUDServiceTest{


    @Qualifier("CRUDServiceImpl") @Autowired
    CRUDService service ;

    private final DomainUtils.TABLE[] orderToTruncate =
            {INVENTORIES
                    , WAREHOUSES
                    , LOCATIONS
                    , COUNTRIES
                    , REGIONS
                    , PRODUCTS
                    , PRODUCT_CATEGORIES
                    , ORDER_ITEMS
                    , ORDERS
                    , CONTACTS
                    , CUSTOMERS
                    , COMMENTS
                    , BOARDS
                    , EMPLOYEES};
    private final DomainUtils.TABLE[] orderToInsert =
            {REGIONS
                    , COUNTRIES
                    , LOCATIONS
                    , WAREHOUSES
                    , PRODUCT_CATEGORIES
                    , PRODUCTS
                    , INVENTORIES
                    , CUSTOMERS
                    , CONTACTS
                    , EMPLOYEES
                    , ORDERS
                    , ORDER_ITEMS
                    , BOARDS
                    , COMMENTS};

    @DisplayName("해당 테이블 컬럼 수")
    @Test
    public void count() {
        try {
            Arrays.stream(orderToTruncate).forEach(table -> {
                try {
                    assertNotEquals(0,service.count(table));
                } catch (Exception e) {
                    //            e.printStackTrace();
                }
            });

        } catch (Exception e) {
            //      e.printStackTrace();
        }
    }

    @DisplayName("해당 테이블 모든 컬럼 삭제")
    @Test
    public void delete() {
        Arrays.stream(orderToTruncate).forEach(table -> {
            try {
                service.delete(table);
                assertNotEquals(0,service.count(table));
            } catch (Exception e) {
                //           e.printStackTrace();
            }
        });
    }


    @DisplayName("해당 테이블 모든 컬럼 셀렉트")
    @Test
    public void select() throws Exception {
//       TABLE table
        Arrays.stream(orderToTruncate).forEach(table ->
        {
            try {
                Collection<? extends Domain> select = service.select(table);
                assertNotEquals(0, select.size());
            } catch (Exception e) {
                //        e.printStackTrace();
            }
        });
    }


    @DisplayName("식별자로 해당 테이블 컬럼 삭제")
    @Test
    public void deleteById() {
        try {
//        식별 번호 210, 8 인 컬럼 삭제
            boolean delete = service.delete(INVENTORIES, new BigInteger[]{BigInteger.valueOf(210), BigInteger.valueOf(8)});
//        삭제 성공 검증
            assertTrue(delete);
//        식별 번호 70, 7 인 컬럼 삭제
            delete = service.delete(ORDER_ITEMS, new BigInteger[]{BigInteger.valueOf(70), BigInteger.valueOf(7)});
//        삭제 성공 검증
            assertTrue(delete);
        } catch (Exception e) {
            //      e.printStackTrace();
        }

//        식별 번호 2인 컬럼 삭제
        Arrays.stream(orderToTruncate).forEach(table -> {
            if (!(ORDER_ITEMS == table || INVENTORIES == table))
                try {
                    //                    삭제 성공 검증
                    assertTrue(service.delete(table, BigInteger.TWO));


                } catch (Exception e) {
                    //                 e.printStackTrace();
                }
        });


        Domain domain = null;
//     식별 번호 2인 컬럼 선택
        for (DomainUtils.TABLE table : orderToInsert) {

            if (!(table == INVENTORIES || table == ORDER_ITEMS))

                try {
                    domain = service.select(table, BigInteger.TWO);
                } catch (Exception e) {
//                    null 체크
                    assertNull(domain);

                }
        }


        try {
//     ORDER_ITEMS 삭제된 컬럼 선택
            domain = service.select(ORDER_ITEMS, new BigInteger[]{BigInteger.valueOf(70), BigInteger.valueOf(7)});

        } catch (Exception e) {
//            null 체크
            assertNull(domain);
        }

        try {
//     INVENTORIES 삭제된 컬럼 선택
            domain = service.select(INVENTORIES, new BigInteger[]{BigInteger.valueOf(210), BigInteger.valueOf(8)});

        } catch (Exception e) {
            assertNull(domain);
        }

    }


    @DisplayName("식별자로 컬럼의 존재여부")
    @Test
    public void isExist() throws Exception {
        Arrays.stream(orderToInsert).forEach(table -> {
            if (!(table == ORDER_ITEMS || table == INVENTORIES)) {
                try {
                    Boolean exist = service.isExist(table, BigInteger.ONE);
                    assertTrue(exist);
                } catch (Exception e) {
                    //              e.printStackTrace();
                }
            }
        });

        Boolean exist = service.isExist(ORDER_ITEMS, new BigInteger[]{BigInteger.valueOf(70), BigInteger.valueOf(7)});
        assertTrue(exist);

        exist = service.isExist(INVENTORIES, new BigInteger[]{BigInteger.valueOf(210), BigInteger.valueOf(8)});
        assertTrue(exist);
    }


    @DisplayName("식별자로 컬럼 셀렉트")
    @Test
    public void selectById() throws Exception {
        Arrays.stream(orderToInsert).forEach(table -> {
            if (!(table == INVENTORIES || table == ORDER_ITEMS))
                try {
                    Domain domain = service.select(table, BigInteger.ONE);
                    assertNotNull(domain);
                } catch (Exception e) {
                    //            e.printStackTrace();
                }

        });

        try {
            Domain domain = service.select(ORDER_ITEMS, new BigInteger[]{BigInteger.valueOf(70), BigInteger.valueOf(7)});
            assertNotNull(domain);

            domain = service.select(INVENTORIES, new BigInteger[]{BigInteger.valueOf(210), BigInteger.valueOf(8)});

            assertNotNull(domain);
        } catch (Exception e) {
            //    e.printStackTrace();
        }
    }


    @DisplayName("컬럼 업데이트")
    @Test
    public void update() {
        try {
//            CUSTOMERS 이름 변경, 검증
//            식별 번호 1
            Domain domain = service.select(CUSTOMERS, BigInteger.ONE);
//             NULL 이 아닌지
            assertNotNull(domain);

            Customer customer = (Customer) domain;
            final String newName = "타미 김";
            customer.setName(newName);
            assertTrue(service.update(customer));

            domain = service.select(CUSTOMERS, BigInteger.ONE);
            assertEquals(newName, ((Customer) domain).getName());

//            BOARDS 제목 변경, 검증
//            식별 번호 1
            domain = service.select(BOARDS, BigInteger.ONE);
//             NULL 이 아닌지
            assertNotNull(domain);

            Board board = (Board) domain;
            final String newTitle = "변경된 제목";

            board.setTitle(newTitle);

            assertTrue(service.update(board));

            domain = service.select(BOARDS, BigInteger.ONE);
            assertEquals(newTitle, ((Board) domain).getTitle());


//            COMMENTS 제목 변경, 검증
//            식별 번호 1
            domain = service.select(COMMENTS, BigInteger.ONE);
            assertNotNull(domain);

            Comment comment = (Comment) domain;
            final String newContent = "변경된 내용";
            comment.setContent(newContent);
            assertTrue(service.update(comment));

            domain = service.select(COMMENTS, BigInteger.ONE);
            assertEquals(newContent, ((Comment) domain).getContent());

//            EMPLOYEES 이메일 변경, 검증
            domain = service.select(EMPLOYEES, BigInteger.ONE);
            assertNotNull(domain);
            Employee employee = (Employee) domain;
            final String newEmail = "A@A.A";
            employee.setEmail(newEmail);
            service.update(employee);

            domain = service.select(EMPLOYEES, BigInteger.ONE);
            assertEquals(newEmail, ((Employee) domain).getEmail());

//            ORDERS 상태 변경,검증
            domain = service.select(ORDERS, BigInteger.ONE);
            Order order = (Order) domain;
            final String newStatus = "Canceled";
            Order newOrder = new Order(order.getOrderId(), order.getCustomerId(), order.getSalesmanId(), newStatus, order.getOrderDate());
            assertTrue(service.update(newOrder));

            domain = service.select(ORDERS, BigInteger.ONE);
            assertEquals(newStatus, ((Order) domain).getStatus());

//            CONTACT 이메일 변경,검증
            domain = service.select(CONTACTS, BigInteger.ONE);
            Contact contact = (Contact) domain;
            final String newEmail2 = "B@B.B";
            contact.setEmail(newEmail2);
            assertTrue(service.update(contact));

            domain = service.select(CONTACTS, BigInteger.ONE);
            assertEquals(newEmail2, ((Contact) domain).getEmail());

//            ORDER_ITEM 수량,가격 변경,검증
            domain = service.select(ORDER_ITEMS, new BigInteger[]{BigInteger.ONE, BigInteger.ONE});
            assertNotNull(domain);
            OrderItem orderItem = (OrderItem) domain;
            final double newQuantity = orderItem.getQuantity() + 10;
            final double newUnitPrice = orderItem.getUnitPrice() * 10;
            OrderItem orderItem1 = new OrderItem(orderItem.getOrderId(), orderItem.getItemId(), orderItem.getProductId()
                    , newQuantity, newUnitPrice);

            assertTrue(service.update(orderItem));

            domain = service.select(ORDER_ITEMS, new BigInteger[]{BigInteger.ONE, BigInteger.ONE});
            assertEquals(newQuantity, ((OrderItem) domain).getQuantity());
            assertEquals(newUnitPrice, ((OrderItem) domain).getUnitPrice());

            domain = service.select(ORDER_ITEMS, new BigInteger[]{BigInteger.ONE, BigInteger.ONE});

//            PRODUCT 표준 가격 변경, 검증
            domain = service.select(PRODUCTS, BigInteger.ONE);
            Product product = (Product) domain;
            final double newStanCost = 3000.0;
            product.setStandardCost(newStanCost);
            assertTrue(service.update(product));

            domain = service.select(PRODUCTS, BigInteger.ONE);
            assertEquals(newStanCost, ((Product) domain).getStandardCost());

//            PRODUCT_CATEGORIES 이름 변경,검증
            domain = service.select(PRODUCT_CATEGORIES, BigInteger.ONE);
//            System.out.println("domain = " + domain);
            ProductCategory productCategory = (ProductCategory) domain;
            final String newCategoryName = "씨피유";
            productCategory.setCategoryName(newCategoryName);
            assertTrue(service.update(productCategory));

            domain = service.select(PRODUCT_CATEGORIES, BigInteger.ONE);
            assertEquals(newCategoryName, ((ProductCategory) domain).getCategoryName());

//            INVENTORIES 수량 변경,검증
            domain = service.select(INVENTORIES, new BigInteger[]{BigInteger.valueOf(210), BigInteger.valueOf(8)});
            assertNotNull(domain);
            Inventory inventory = (Inventory) domain;

            final BigInteger newQuantity2 = inventory.getQuantity().add(BigInteger.valueOf(100));
            inventory.setQuantity(newQuantity2);
            assertTrue(service.update(inventory));

            domain = service.select(INVENTORIES, new BigInteger[]{BigInteger.valueOf(210), BigInteger.valueOf(8)});
            assertEquals(newQuantity2, ((Inventory) domain).getQuantity());

//            WAREHOUSE 웨어하우스 이름 변경,검증
            domain = service.select(WAREHOUSES, BigInteger.ONE);
            assertNotNull(domain);
            Warehouse warehouse = (Warehouse) domain;
            final String newWarehousename = "Texas";
            warehouse.setWarehouseName(newWarehousename);
            assertTrue(service.update(warehouse));

            domain = service.select(WAREHOUSES, BigInteger.ONE);
            assertEquals(newWarehousename, ((Warehouse) domain).getWarehouseName());

//          LOCATIONS 주소 변경,검증
            domain = service.select(LOCATIONS, BigInteger.ONE);
            assertNotNull(domain);
            Location location = (Location) domain;
            final String newAddress = location.getAddress().replace("1297", "1300");

            location.setAddress(newAddress);
            assertTrue(service.update(location));

            domain = service.select(LOCATIONS, BigInteger.ONE);
            assertEquals(newAddress, ((Location) domain).getAddress());

//            COUNTRIES.REGION_ID 변경,검증
            domain = service.select(COUNTRIES, BigInteger.ONE);
            assertNotNull(domain);
            Country country = (Country) domain;
            final BigInteger regionId = BigInteger.TWO;
            country.setRegionId(regionId);
            assertTrue(service.update(country));
            assertEquals(regionId, country.getRegionId());

//            REGION.REGION_NAME 변경,검증
            domain = service.select(REGIONS, BigInteger.ONE);
            assertNotNull(domain);
            Region region = (Region) domain;
            final String newRegion = "East Europe";
            region.setRegionName(newRegion);
            assertTrue(service.update(region));

            domain = service.select(REGIONS, BigInteger.ONE);
            assertEquals(newRegion, ((Region) domain).getRegionName());

        } catch (Exception e) {
            //     e.printStackTrace();
        }
    }

    @DisplayName("컬럼 삽입")
    @Test
    public void insert() throws Exception {
        Region newRegion = new Region(BigInteger.valueOf(5), "North Pole");
        Country newCountry = new Country(BigInteger.valueOf(27), BigInteger.valueOf(5), "North Pole");
        Location newLocation = new Location(BigInteger.valueOf(24), BigInteger.valueOf(27),
                "North 99", "1K", "North Pole", "North Pole");
        Warehouse newWarehouse = new Warehouse(BigInteger.TEN, BigInteger.valueOf(24), "North Pole");
        ProductCategory newProductCategory = new ProductCategory(BigInteger.valueOf(6), "Hardware");
        Product newProduct = new Product(BigInteger.valueOf(289), BigInteger.valueOf(6)
                , "Samsung DU-DZA5D3X/HW", "Seris:gram metal outside GTX", 3000.00, 5000.00);
        Inventory newInventory = new Inventory(BigInteger.valueOf(289), BigInteger.valueOf(6), BigInteger.valueOf(500));
        Customer newCustomer = new Customer(BigInteger.valueOf(1001), "Sam Smith", "Sanfranscio 287 road house",
                "https://www.google.com", 3000.00);
        Employee newEmployee = new Employee(BigInteger.valueOf(1108), BigInteger.valueOf(50),
                "Shon", "Alen", "Shao@google.com", "013-2213-5555", "location aide",
                LocalDate.of(2005, 10, 25));
        Order newOrder = new Order(BigInteger.valueOf(106), BigInteger.valueOf(1001),
                BigInteger.valueOf(30), "Canceled", LocalDate.of(2005, 11, 3));
        OrderItem newOrderItem = new OrderItem(BigInteger.valueOf(106), BigInteger.valueOf(3),
                BigInteger.valueOf(103), 30.00, 50.99);
        Board newBoard = new Board(BigInteger.valueOf(1008), "Son Alen", "hi everyOne", "thank");
        Contact newContact = new Contact(BigInteger.valueOf(600), "Sam", "Smith", "samS33@google.com",
                "013-0031-2211", BigInteger.valueOf(1001));
        Comment newComment = new Comment(BigInteger.valueOf(96), BigInteger.valueOf(1001),
                BigInteger.valueOf(1008), BigInteger.valueOf(96), "new comment",
                "Shon Alen", LocalDateTime.now(), null);

//        데이터 삽입
        assertTrue(service.insert(newRegion));
        assertTrue(service.insert(newCountry));
        assertTrue(service.insert(newLocation));
        assertTrue(service.insert(newWarehouse));
        assertTrue(service.insert(newProductCategory));
        assertTrue(service.insert(newProduct));
        assertTrue(service.insert(newInventory));
        assertTrue(service.insert(newCustomer));
        assertTrue(service.insert(newContact));
        assertTrue(service.insert(newEmployee));
        assertTrue(service.insert(newOrder));
        assertTrue(service.insert(newOrderItem));
        assertTrue(service.insert(newBoard));
        assertTrue(service.insert(newComment));

//        삽입한 데이터 널 체크
        assertNotNull(service.select(REGIONS, newRegion.getRegionId()));
        assertNotNull(service.select(COUNTRIES, newCountry.getCountryId()));
        assertNotNull(service.select(LOCATIONS, newLocation.getLocationId()));
        assertNotNull(service.select(WAREHOUSES, newWarehouse.getWarehouseId()));
        assertNotNull(service.select(PRODUCT_CATEGORIES, newProductCategory.getCategoryId()));
        assertNotNull(service.select(PRODUCTS, newProduct.getProductId()));
        assertNotNull(service.select(INVENTORIES, newInventory.getProductId(), newInventory.getWarehouseId()));
        assertNotNull(service.select(CUSTOMERS, newCustomer.getCustomerId()));
        assertNotNull(service.select(CONTACTS, newContact.getContactId()));
        assertNotNull(service.select(EMPLOYEES, newEmployee.getEmployeeId()));
        assertNotNull(service.select(ORDERS, newOrder.getOrderId()));
        assertNotNull(service.select(ORDER_ITEMS, newOrderItem.getOrderId(), newOrderItem.getItemId()));
        assertNotNull(service.select(BOARDS, BigInteger.valueOf(1001)));
        assertNotNull((Comment) service.select(COMMENTS, BigInteger.valueOf(96)));
    }


//    @Test
//    public void aVoid()
//    {
//        String regex="/employee/([0-9])\\w*/board/([0-9])\\w*";
//        String requestURI="/employee/9/board/9";
//        System.out.println(Pattern.matches(regex, requestURI));
//
//        String regex2="/employee/([0-9])\\w*/board";
//        String requestURI2="/employee/9/board";
//        System.out.println(Pattern.matches(regex2, requestURI2));
//
//    }
}

