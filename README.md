# TRACKY - 실시간 차량 관제 서비스

<img src="https://github.com/Kernel360/KDEV4-TRACKY-BE/blob/resource/img-tracky/image.png?raw=true" width="400">

서비스 체험하기 - [https://www.tracky.kr](https://www.tracky.kr)<br/>
PDF - [https://www.canva.com/design/DAGnVKhYFmw/2JCuVRr8JMHjKqibKvbuIQ/edit](https://www.canva.com/design/DAGnVKhYFmw/2JCuVRr8JMHjKqibKvbuIQ/edit)<br/>
ERD - [https://www.erdcloud.com/d/mE2uimBHiXsvoxAWJ](https://www.erdcloud.com/d/mE2uimBHiXsvoxAWJ)



## TRACKY 소개
차량 운영에 어려움을 겪는 기업을 위한 **통합 관제 솔루션**입니다.

준 실시간 위치 추적, 운행 기록 조회, 다양한 통계 데이터 등을 제공함으로써

누가, 언제, 어디서, 얼마나 차량을 사용했는지 확인할 수 있습니다.

## 기술 스택
### BE
- Java17, Spring Boot, Spring Security, JWT
- Spring Data JPA, QueryDSL, SSE, RabbitMQ
- Amazon ECS, Amazon RDS, Amazon ALB


### FE
- React, Typescript, Vercel 


## 주요 기능

**대시보드**

![Image](https://github.com/user-attachments/assets/2460c131-a4ab-4194-b7f5-7d6cfc251c2c)

**차량관리**

![image](https://github.com/user-attachments/assets/4a340ef1-06a9-46d1-a23c-1fe145fc3ee4)


**준 실시간 경로 추적**

![image](https://github.com/user-attachments/assets/b1049061-a66b-483f-8a3a-a33856afacc2)



**운행기록 조회**

![image](https://github.com/user-attachments/assets/ed011f62-7f4e-4d07-b847-11bcfd7a3cce)



**사용자 및 관리자 통계**

![image](https://github.com/user-attachments/assets/b80b6fa8-0a30-4958-8f8d-45baa4505353)
![image](https://github.com/user-attachments/assets/e59c7f7b-fc92-492d-b9da-05161fd0186c)





## 시스템 아키텍처
![image](https://github.com/user-attachments/assets/dd4dae3e-c694-4340-bb05-5da51b74ab86)
---
![Image](https://github.com/user-attachments/assets/fccb1d2b-671f-4e15-8ca6-782d31b58f95)


## 이슈 및 트러블 슈팅
- [부하테스트 - 15,000 TPS 수용하기](https://www.notion.so/1cea3f519ab880979ae1c736bf9f0198)
  - hub, consumer 구조 채택 이유
  - 테스트 시나리오, 환경, 결과 도출 과정
  - 테스트 도중 발생한 트러블 슈팅

- [복합 인덱스를 통한 쿼리 최적화 과정](https://github.com/Kernel360/KDEV4-TRACKY-BE/wiki/%EB%B3%B5%ED%95%A9-%EC%9D%B8%EB%8D%B1%EC%8A%A4%EB%A5%BC-%ED%86%B5%ED%95%9C-%EC%A1%B0%ED%9A%8C-%EC%BF%BC%EB%A6%AC-%EC%B5%9C%EC%A0%81%ED%99%94-%EA%B3%BC%EC%A0%95)


## 팀원소개
<table>
  <tbody>
    <tr>
      <td align="center">
        <a href="https://github.com/jun950829">
          <img src="https://avatars.githubusercontent.com/u/55969783?v=4" width="100px;" alt="팀장 프로필"/><br />
          <span><b>천승준</b></span>
        </a>
      </td>
      <td align="center">
        <a href="https://github.com/ji-won-2">
          <img src="https://avatars.githubusercontent.com/u/200579267?v=4" width="100px;" alt="팀원 프로필"/><br />
          <span><b>구지원</b></span>
        </a>
      </td>
      <td align="center">
        <a href="https://github.com/Tojaman">
          <img src="https://avatars.githubusercontent.com/u/89668546?v=4" width="100px;" alt="팀원 프로필"/><br />
          <span><b>조형준</b></span>
        </a>
      </td>
      <td align="center">
        <a href="https://github.com/devJeenee">
          <img src="https://avatars.githubusercontent.com/u/95906139?v=4" width="100px;" alt="팀원 프로필"/><br />
          <span><b>차의진</b></span>
        </a>
      </td>
      <td align="center">
        <a href="https://github.com/kimchijinju">
          <img src="https://avatars.githubusercontent.com/u/85796402?v=4" width="100px;" alt="팀원 프로필"/><br />
          <span><b>김한빈</b></span>
        </a>
      </td>
      <td align="center">
        <a href="https://github.com/ongsttt52">
          <img src="https://avatars.githubusercontent.com/u/177156866?v=4" width="100px;" alt="팀원 프로필"/><br />
          <span><b>오승택</b></span>
        </a>
      </td>
    </tr>
  </tbody>
</table>



