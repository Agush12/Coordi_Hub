//package com.project.controller;
//
//import com.project.exception.ChatException;
//import com.project.exception.ProjectException;
//import com.project.exception.UserException;
//import com.project.model.Chat;
//import com.project.model.Message;
//import com.project.model.User;
//import com.project.request.CreateMessageRequest;
//import com.project.service.MessageService;
//import com.project.service.ProjectService;
//import com.project.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/messages")
//public class RealTimeChatController {
//
//    @Autowired
//    private MessageService messageService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    // REST endpoints for traditional HTTP requests
//    @PostMapping("/send")
//    public ResponseEntity<Message> sendtoMessage(@RequestBody CreateMessageRequest request)
//            throws UserException, ChatException, ProjectException {
//        User user = userService.findUserById(request.getSenderId());
//        if (user == null) {
//            throw new UserException("User not found with id " + request.getSenderId());
//        }
//
//        Chat chat = projectService.getProjectById(request.getProjectId()).getChat();
//        if (chat == null) {
//            throw new ChatException("Chat not found");
//        }
//
//        Message sentMessage = messageService.sendMessage(
//                request.getSenderId(),
//                request.getProjectId(),
//                request.getContent()
//        );
//
//        // Broadcast the message to all subscribers of this project's chat
//        messagingTemplate.convertAndSend("/chat/" + request.getProjectId(), sentMessage);
//
//        return ResponseEntity.ok(sentMessage);
//    }
//
//    @GetMapping("/chat/{projectId}")
//    public ResponseEntity<List<Message>> getMessagesByChatId(@PathVariable Long projectId)
//            throws ProjectException, ChatException {
//        List<Message> messages = messageService.getMessagesByProjectId(projectId);
//        return ResponseEntity.ok(messages);
//    }
//
//    // WebSocket endpoints for real-time messaging
//    @MessageMapping("/chat/{projectId}")
//    public void handleWebSocketMessage(
//            @Payload CreateMessageRequest request,
//            @DestinationVariable Long projectId
//    ) throws UserException, ChatException, ProjectException {
//        try {
//            User user = userService.findUserById(request.getSenderId());
//            if (user == null) {
//                throw new UserException("User not found with id " + request.getSenderId());
//            }
//
//            Chat chat = projectService.getProjectById(projectId).getChat();
//            if (chat == null) {
//                throw new ChatException("Chat not found");
//            }
//
//            Message message = messageService.sendMessage(
//                    request.getSenderId(),
//                    projectId,
//                    request.getContent()
//            );
//
//            // Send to project chat channel
//            messagingTemplate.convertAndSend("/chat/" + projectId, message);
//
//            // Send to specific user channels if needed
//            messagingTemplate.convertAndSendToUser(
//                    request.getSenderId().toString(),
//                    "/private",
//                    message
//            );
//
//        } catch (Exception e) {
//            // Send error message back to sender
//            messagingTemplate.convertAndSendToUser(
//                    request.getSenderId().toString(),
//                    "/error",
//                    e.getMessage()
//            );
//        }
//    }
//
//    // Additional WebSocket endpoint for user-specific messages
//    @MessageMapping("/private/{userId}")
//    public void handlePrivateMessage(
//            @Payload CreateMessageRequest request,
//            @DestinationVariable Long userId
//    ) throws UserException, ChatException {
//        try {
//            Message message = messageService.sendMessage(
//                    request.getSenderId(),
//                    request.getProjectId(),
//                    request.getContent()
//            );
//
//            // Send to specific user's private channel
//            messagingTemplate.convertAndSendToUser(
//                    userId.toString(),
//                    "/private",
//                    message
//            );
//        } catch (Exception e) {
//            messagingTemplate.convertAndSendToUser(
//                    request.getSenderId().toString(),
//                    "/error",
//                    e.getMessage()
//            );
//        }
//    }
//}