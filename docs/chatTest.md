# chatTest 가이드

```javascript
 // WebSocket 채팅 테스트 가이드

  // 1. 초기 설정 - 라이브러리 로드 및 연결

  // Step 1: 라이브러리 로드
  const script1 = document.createElement('script');
  script1.src = 'https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js';
  document.head.appendChild(script1);

  const script2 = document.createElement('script');
  script2.src = 'https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js';
  document.head.appendChild(script2);

  // Step 2: WebSocket 연결 (2초 후 실행)
  setTimeout(() => {
      window.socket = new SockJS('/chat');
      window.stompClient = Stomp.over(socket);

      stompClient.connect({},
          (frame) => {
              console.log('✅ WebSocket 연결 성공!');
          },
          (error) => {
              console.error('❌ WebSocket 연결 실패:', error);
          }
      );
  }, 2000);

 // 2. 채팅방 생성

  // 채팅방 생성 함수
  function createChatRoom(productId, userId) {
      return fetch('/chat/rooms', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json'
          },
          body: JSON.stringify({
              productId: productId || 1,
              userId: userId || 1
          })
      })
      .then(res => res.json())
      .then(data => {
          console.log('✅ 채팅방 생성 완료:', data);
          window.currentRoomId = data.id || data.chatRoomId;
          return window.currentRoomId;
      });
  }

  // 실행
  createChatRoom(1, 1);

  // 3. 채팅방 구독

  // 채팅방 구독 함수
  function subscribeToChatRoom(roomId) {
      if (window.subscription) {
          window.subscription.unsubscribe();
          console.log('기존 구독 해제');
      }

      window.subscription = stompClient.subscribe(`/sub/chat/room/${roomId}`, (message) => {
          const msg = JSON.parse(message.body);
          console.log(`💬 [${msg.senderType}] ${msg.senderName}: ${msg.content}`);
          console.log('전체 메시지 정보:', msg);
      });

      console.log(`📡 채팅방 ${roomId}번 구독 시작`);
  }

  // 실행 (채팅방 생성 후)
  subscribeToChatRoom(window.currentRoomId);

  // 4. 게스트 메시지 발신

  // 게스트 메시지 전송 함수
  function sendGuestMessage(content) {
      if (!window.currentRoomId) {
          console.error('❌ 채팅방을 먼저 생성하세요!');
          return;
      }

      stompClient.send('/pub/chat/send', {}, JSON.stringify({
          chatRoomId: window.currentRoomId,
          senderType: 'GUEST',
          content: content
      }));

      console.log(`📤 게스트 메시지 전송: ${content}`);
  }

  // 실행
  sendGuestMessage('안녕하세요, 상품 문의드립니다.');
  sendGuestMessage('이 상품 재고가 있나요?');

  // 5. 관리자 답변

  // 관리자 메시지 전송 함수
  function sendAdminMessage(content) {
      if (!window.currentRoomId) {
          console.error('❌ 채팅방을 먼저 생성하세요!');
          return;
      }

      stompClient.send('/pub/chat/send', {}, JSON.stringify({
          chatRoomId: window.currentRoomId,
          senderType: 'ADMIN',
          content: content,
          senderId: 'admin123'  // 관리자 ID
      }));

      console.log(`📤 관리자 답변 전송: ${content}`);
  }

  // 실행
  sendAdminMessage('안녕하세요, 무엇을 도와드릴까요?');
  sendAdminMessage('네, 현재 재고가 10개 있습니다.');

  // 6. 전체 테스트 시나리오 (한 번에 실행)

  // 전체 테스트 함수
  async function runFullChatTest() {
      console.log('🚀 채팅 테스트 시작...\n');

      // 1. 라이브러리 로드
      const script1 = document.createElement('script');
      script1.src = 'https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js';
      document.head.appendChild(script1);

      const script2 = document.createElement('script');
      script2.src = 'https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js';
      document.head.appendChild(script2);

      // 2초 대기 (라이브러리 로드)
      await new Promise(resolve => setTimeout(resolve, 2000));

      // 2. WebSocket 연결
      window.socket = new SockJS('/chat');
      window.stompClient = Stomp.over(socket);

      return new Promise((resolve) => {
          stompClient.connect({}, async (frame) => {
              console.log('✅ WebSocket 연결 성공!\n');

              // 3. 채팅방 생성
              const roomData = await fetch('/chat/rooms', {
                  method: 'POST',
                  headers: {'Content-Type': 'application/json'},
                  body: JSON.stringify({productId: 1, userId: 1})
              }).then(res => res.json());

              const roomId = roomData.id || roomData.chatRoomId;
              console.log(`✅ 채팅방 생성 완료! (ID: ${roomId})\n`);

              // 4. 채팅방 구독
              stompClient.subscribe(`/sub/chat/room/${roomId}`, (message) => {
                  const msg = JSON.parse(message.body);
                  console.log(`💬 [${msg.senderType}] ${msg.senderName}: ${msg.content}`);
              });
              console.log('📡 채팅방 구독 시작\n');

              // 5. 대화 시뮬레이션
              setTimeout(() => {
                  console.log('--- 대화 시작 ---\n');

                  // 게스트 메시지
                  stompClient.send('/pub/chat/send', {}, JSON.stringify({
                      chatRoomId: roomId,
                      senderType: 'GUEST',
                      content: '안녕하세요, 이 상품 문의드립니다.'
                  }));

                  // 관리자 응답 (2초 후)
                  setTimeout(() => {
                      stompClient.send('/pub/chat/send', {}, JSON.stringify({
                          chatRoomId: roomId,
                          senderType: 'ADMIN',
                          content: '안녕하세요! 무엇을 도와드릴까요?'
                      }));
                  }, 2000);

                  // 게스트 추가 질문 (4초 후)
                  setTimeout(() => {
                      stompClient.send('/pub/chat/send', {}, JSON.stringify({
                          chatRoomId: roomId,
                          senderType: 'GUEST',
                          content: '재고가 있나요?'
                      }));
                  }, 4000);

                  // 관리자 답변 (6초 후)
                  setTimeout(() => {
                      stompClient.send('/pub/chat/send', {}, JSON.stringify({
                          chatRoomId: roomId,
                          senderType: 'ADMIN',
                          content: '네, 현재 10개의 재고가 있습니다!'
                      }));
                  }, 6000);

              }, 1000);

              resolve();
          });
      });
  }

  // 테스트 실행
  runFullChatTest();

  // 7. 유틸리티 함수들

  // 현재 채팅방 목록 조회
  function listChatRooms() {
      fetch('/chat/rooms')
          .then(res => res.json())
          .then(data => console.log('📋 채팅방 목록:', data));
  }

  // 특정 채팅방의 메시지 히스토리 조회
  function getChatHistory(roomId) {
      fetch(`/chat/rooms/${roomId}/messages`)
          .then(res => res.json())
          .then(data => console.log('📜 메시지 히스토리:', data));
  }

  // WebSocket 연결 상태 확인
  function checkConnection() {
      if (window.stompClient && window.stompClient.connected) {
          console.log('✅ WebSocket 연결 상태: 연결됨');
      } else {
          console.log('❌ WebSocket 연결 상태: 연결 안됨');
      }
  }

  // 연결 종료
  function disconnect() {
      if (window.stompClient) {
          window.stompClient.disconnect(() => {
              console.log('👋 WebSocket 연결 종료');
          });
      }
  }


```
