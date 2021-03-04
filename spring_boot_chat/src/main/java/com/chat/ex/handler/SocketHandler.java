package com.chat.ex.handler;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler { //웹소켓 연결시 동작

	HashMap<String, WebSocketSession> sessionMap = new HashMap<>(); //웹소켓 세션 다아둘 맵
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		//메세지 발송, 메세지를 수신하면 실행
		
		String msg = message.getPayload();
		//메시지 전송시 json파싱을 위해 message.getPayload()를 통해 받은 문자열을 
		
		JSONObject obj = JsonToObjectParser(msg);
		//jsonToObjectParser에 넣어서 jsonobject값으로 받아서 강제 문자열 형태로 보내주는 부분
		
		for (String key : sessionMap.keySet()) {
			WebSocketSession wss = sessionMap.get(key);
			try {
				wss.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//소켓 연결
		
		super.afterConnectionEstablished(session);
		sessionMap.put(session.getId(), session);
		JSONObject obj = new JSONObject();
		obj.put("type", "getId");//세션 저장시 발신메시지 타입 getId 라고 명시 -> 생성된 세션id를 클라이언트단으로 발송
		obj.put("sessionId", session.getId());//클라이언트 단에서는 type값을 통해 메시지와 초기 설정값을 구분
		
		session.sendMessage(new TextMessage(obj.toJSONString()));
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		//소켓 종료, 웹소켓 종료시 동작
		sessionMap.remove(session.getId());
		super.afterConnectionClosed(session, status);
	}
	
	private static JSONObject JsonToObjectParser(String jsonStr) { //json 파싱 메소드
		// json형태 파라미터를 받아 simplejson의 파서를 활용해 jsonobject 파싱처리
		
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj =(JSONObject)parser.parse(jsonStr);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return obj;
		
	}
	
}
