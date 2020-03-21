package com.frontend.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frontend.domain.MCAppInfo;
import com.frontend.mappers.ProjectMapper;
import com.frontend.models.LogMessage;
import com.frontend.models.MCDeviceInfo;
import com.frontend.models.MCMessage;
import com.frontend.models.NetLogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MCWebSocketHandler extends BinaryWebSocketHandler {
  Logger logger = LoggerFactory.getLogger(MCWebSocketHandler.class);

  /**
   * Web端会话合集
   */
  private static HashMap<String, WebSocketSession> webSessions = new HashMap<>();

  /**
   * 客户端会话合集
   */
  static HashMap<String, WebSocketSession> clientSessions = new HashMap<>();

  /**
   * 设备列表
   */
  private static HashMap<String, MCDeviceInfo> devices = new HashMap<>();

  @Autowired
  private ProjectMapper appsMapper;

  public ProjectMapper getAppsMapper() {
    return appsMapper;
  }

  @Autowired
  public void setAppsMapper(ProjectMapper appsMapper) {
    this.appsMapper = appsMapper;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    if ("web".equalsIgnoreCase((String) session.getAttributes().get("source"))) {
      webSessions.put(session.getId(), session);
    } else {
      clientSessions.put(session.getId(), session);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    if ("web".equalsIgnoreCase((String) session.getAttributes().get("source"))) {
      webSessions.remove(session.getId());
    } else {
      clientSessions.remove(session.getId());
      devices.remove(session.getAttributes().get("deviceid"));
    }
  }

  @Override
  protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
    MCMessage msg = JSON.parseObject(message.getPayload().array(), MCMessage.class);
    if (msg != null) {
      int type = msg.getType();
      try {
        Method method = this.getClass().getDeclaredMethod("messageHandlerForType" + type, MCMessage.class, WebSocketSession.class);
        String result = (String) method.invoke(this, msg, session);
        if (result != null) {
          session.sendMessage(new BinaryMessage(ByteBuffer.wrap(result.getBytes())));
        }
      } catch (NoSuchMethodException e) {
        logger.error("无法处理消息: " + type, e);
      } catch (IllegalAccessException e) {
        logger.error(e.getLocalizedMessage(), e.getCause());
      } catch (InvocationTargetException e) {
        logger.error(e.getLocalizedMessage(), e.getCause());
      }
    }
  }

  @Override
  protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    super.handlePongMessage(session, message);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    super.handleTransportError(session, exception);
  }

  /**
   * 注册设备信息 - invoke
   */
  void messageHandlerForType10(MCMessage msg, WebSocketSession session) {
    MCDeviceInfo info = ((JSON) msg.getData()).toJavaObject(MCDeviceInfo.class);
    if (info.getDeviceId() == null) {
      logger.error("注册设备必须包含deviceId");
    } else {
//      MCAppInfo appInfo = appsMapper.findByAppKey(info.getAppKey());
//      if (appInfo != null) {
//        info.setAppId(appInfo.getId());
//      }
      devices.put(info.getDeviceId(), info);
      logger.info("设备注册成功: " + info.getDeviceId());
    }
  }

  void messageHandlerForType11(MCMessage msg, WebSocketSession session) {
    List<MCDeviceInfo> list = new ArrayList<>();
    for(MCDeviceInfo device : devices.values()) {
      if (device.getAppKey().equalsIgnoreCase(msg.getAppKey())) {
        list.add(device);
      }
    }
    try {
      msg.setData(list);
      session.sendMessage(new BinaryMessage(JSON.toJSONBytes(msg)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 客户端日志（终端）
   *
   * @param msg
   */
  public void messageHandlerForType30(MCMessage msg, WebSocketSession session) {
    LogMessage logMessage = JSONObject.parseObject(msg.getData().toString(), LogMessage.class);
    if (logMessage != null) {
      for (WebSocketSession destSession : webSessions.values()) {
        String deviceId = (String) session.getAttributes().get("deviceid");
        if (deviceId.equalsIgnoreCase((String) destSession.getAttributes().get("deviceid"))
          && destSession.isOpen()) {
          try {
            destSession.sendMessage(new BinaryMessage(ByteBuffer.wrap(JSON.toJSONBytes(msg))));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * 客户端日志（网络接口)
   *
   * @param msg
   */
  public void messageHandlerForType31(MCMessage msg, WebSocketSession session) {
    NetLogMessage logMessage = JSONObject.parseObject(msg.getData().toString(), NetLogMessage.class);
    if (logMessage != null) {
      for (WebSocketSession destSession : webSessions.values()) {
        String deviceId = (String) session.getAttributes().get("deviceid");
        if (deviceId.equalsIgnoreCase((String) destSession.getAttributes().get("deviceid"))
          && destSession.isOpen()) {
          try {
            destSession.sendMessage(new BinaryMessage(ByteBuffer.wrap(JSON.toJSONBytes(msg))));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

}