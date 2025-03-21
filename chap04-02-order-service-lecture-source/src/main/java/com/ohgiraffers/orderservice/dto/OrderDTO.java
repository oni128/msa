package com.ohgiraffers.orderservice.dto;

import lombok.Data;

import java.util.List;

/* 설명.
 *  Query쪽은 DTO로 계층 이동 및 DB와의 CRUD 관련해서까지 다 활용할 수 있지만
 *  DTO가 의미없이 모든 경우의 수를 감당해 내기 보다는 DTO는 요청별로 구분해서
 *  설계하고 Entity 개념의 클래스는 모든 ResultMap의 결과를 받아 줄 수 있게
 *  설계하는 것을 추천함
 *  (DTO: 요청별 요청 및 응답 값 담는 용도,
 *   Entity 개념의 클래스: 모든 ResultMap의 결과를 받아줄 수 있는 용도)
* */
@Data
public class OrderDTO {
    private int orderCode;
    private int userId;
    private String orderDate;
    private String orderTime;
    private int totalOrderPrice;

    private List<OrderMenuDTO> orderMenus;
}
