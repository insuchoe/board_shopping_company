## BrandedCompany
### 소개
  소규모 전자상거래 사이트<br/>
  작업기간 : 3개월<br/>
  작업인원 : 1인<br/>
  자바코드 3만라인과 나머지 파일(jsp,css,html...) 의 코드들 1만 라인으로 개발되었습니다.<br/>
  15개의 테이블로 개발되었습니다.<br/>
  나머지 파일들을 포함해 총 약 6만라인의 코드로 개발되었습니다.<br/>
  AWS 로 배포된 상태고 링크는 아래와 같습니다.<br/>
  리포지토리에 첨부한 직원_로그인_데이터.txt,고객_로그인_데이터.txt 에 적힌 데이터로 번호/이름 성 입력하셔서 접속할 수 있습니다.<br/>
  포트폴리오 URL : 43.200.178.92:8080/brandedCompany<br/>
##
### 개발 환경
* IntelliJ
* Oracle 19c
* Apache Tomcat 9
### ERD
![ERD](https://user-images.githubusercontent.com/49363880/192488968-d6854eac-9cc5-47e8-b457-789c8c05eb9e.PNG)
##
### 배포 환경
* AWS EC2
##
### 메인 기술
* java 11
* spring 5
* jsp 2.3
* jQuery 1.11.3
* javaScript es6
* html5
* css3
##
### 특징 기술
* spring 5.0.7 - 프레임 워크
* myBatis 3.5.9 - 프레임 워크
* junit 4.12 - 테스트 프레임 워크
* junit-jupiter 5.8.2 - 테스트 프레임 워크
* mockito-junit-jupiter 2.26.0 - 테스트 프레임 워크
* oracle jdbc 19.3.0.0 - DB 데이터 연동
* commons-codec 1.15 - 이미지 Base64 변환
* jackson-datatype-jsr310 2.13.2 - 데이터 직렬화,역직렬화
* spring-validation 2.0.1 - 로그인 인증기, 동시 접속 차단기, 요청 URL 검사기
* logj4j2 api - 로그 관리
##
### 개발인원
* 최인수
##
### 주요 이슈, 해결법
#### 이슈 1 : 로그인 인증/인가를 어떻게 구현할 것인가?
로그인 요청시 해당 데이터를 검증하는 방법을 고민하다<br/>
스프링 부트에서 사용되는 JWT 를 써야하나 고민한적 있다.<br/>
동영상과 책을 뒤져봐도 스프링에서 JWT 를 쓰는경우를 본적이 없었다.<br/>
확실한 건 스프링 부트로 프로젝트를 다시해야하는 것은 무리수라고 판단했다.<br/>
JWT,Spring Securiy,OAuth 처럼 설계하지는 못하지만<br/>
인증과 인가를 공부한다 셈치고 생각하고 
'그럼 내가 직접 구현해볼까?' 생각이 들었다.<br/>
인증 처리는 Intercpetor 로 DB의 데이터와 대조하여 같으면 인증을 해주고<br/>
인증에 있는 인증객체로 인가처리를 해주면 되겠다는 생각을 했다.<br/>

#### 해결법
##### 인증
로그인 요청시 LoginValidator 가 회원 데이터를 DB 데이터와 일치한지 확인한다.<br/>
일치한다면 회원 데이터를 타겟 객체로 둔 인증객체를 생성하고 AuthenticationRepository 에 저장한다.<br/>
불일치한다면 로그인 페이지로 이동시킨다.
##### 인가
LoginInterceptor 가 AuthenticationRepository 에 저장한 Authentication 의 타겟객체가 DB 데이터와 같은지 검사한다.<br/>
같으면 마이페이지로 이동한다. <br/>
다르다면 AuthenticationRepository 저장된 인증객체를 삭제하고(=인증 무효화) 로그인 페이지로 이동한다.

##
#### 이슈 2 : 동시접속을 어떻게 막을것인가?
누군가 이미 접속중인 회원에 데이터로 재로그인 요청을 했을때<br/>
어떻게 동시접속을 막을 수 있을까? 라는 고민을 했다.<br/>
최초로 누군가 로그인 할 당시 인증/인가과정을 거친다.<br/>
AuthenticationRepository 에 저장한 Authentication 의 유무 기준으로<br/>
'동시접속을 막을 수 있지 않을까' 생각을 했다.<br/>
그리고 접속했을 때 한 계정당 고유의 하나의 인증객체만 부여되고<br/>
로그아웃 할때 인증객체를 제거하기 때문에<br/>
설상 접속중인 회원이 로그아웃 하고 난 뒤<br/>
누군가 같은 회원의 데이터로 로그인을 해도 문제가 되지않는다고 생각을 했다.<br/>
AuthenticationRepository 에 Authentication 이 존재하는지 검사해서<br/>
Authentication 가 있으면 '동시접속' 으로 간주하고,<br/>
Authentication 이 없으면 현재 접속중인 회원이 없는 것으로 간주하기로 했다.

#### 해결법
##### 동시접속 횟수 증가
로그인 요청 시 해당 회원의 AuthenticationRepository에 Authentication 이 있으면<br/>
ConCurrentConnectionRepository 에 회원의 Authentication 을 key 로, 동시접속 횟수를 value 로 저장한다.<br/>
그러고 난 후 로그인 페이지로 리다이렉트 시킨다.<br/>
동시접속 횟수가 3회이상 이라면 로그인 요청을 하지 못하도록 오류 페이지로 이동시킨다.<br/>
AuthenticationRepository 에 Authentication 이 없으면<br/>
이슈1에서 언급한 로그인 인증과 인가 과정을 거친다.<br/>

##### 동시접속 횟수 초기화
접속중인 회원이 로그아웃 요청에 성공한다면<br/>
ConCurrentConnectionRepository 에 Authentication 을 제거한다.(=동시접속 기록삭제)

##
#### 이슈 3 : 프로필 사진을 등록,변경,삭제하는 기능을 어떻게 구현할 것인가?
일반적인 게시글 화면에서 댓글/답글 단 사람들의 프로필 사진을 함께 볼 수 있다.<br/>
이 부분을 똑같이 구현하고 싶었다.<br/>
사진데이터의 보관을 어떻게 할 것인가가 주 고민이었다.<br/>
사진 데이터를 파일 형태로 저장한다거나<br/>
DB에 바이트값을 저장하는 것은 <br/>
엄청난 리소스 낭비라고 생각했다.<br/>
이미지 파일을 글자로 바꿔주는 Base64 로 해당파일을 인코딩하여<br/>
회원아이디.txt 파일을 생성해주고 저장하면 <br/>
상대적으로 리소스절감에 상당히 도움이 된다고 생각했다.

#### 해결법
##### 사진등록,변경
요청시 매개변수로 받은 파일의 바이트값을 Base64 인코딩한다.<br/>
(사진변경)resources/base64 폴더 아래 회원아이디.txt 파일이 존재한다면 인코딩값으로 덮어쓴다.<br/>
(사진등록)회원아이디.txt 파일이 존재하지 않는다면 파일생성 후 인코딩값을 적는다.<br/>
컨트롤러는 최초 로그인시 세션에 저장한 회원객체를 꺼낸다.<br/>
회원객체의 imageBase64 필드의 값을 회원아이디.txt에 있는 base64 인코딩값으로 초기화한다.<br/>
만약 회원아이디.txt 파일이 없다면 noImg.jpg 의 인코딩값으로 초기화한다.
##### 사진삭제
요청한 회원아이디.txt 파일의 값을 noImg.jpg 의 인코딩값으로 덮어씌운다.

##
### 이메일
* cis940320@gmail.com
