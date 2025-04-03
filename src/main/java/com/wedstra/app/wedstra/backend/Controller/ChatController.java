package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.Message;
import com.wedstra.app.wedstra.backend.Repo.MessageRepository;
import com.wedstra.app.wedstra.backend.Services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message){
        if(message.getDate() == null){

        }
        Message uploadedMessage = messageService.uploadMessage(message);
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        return message;
    }

    @GetMapping("/api/messages")
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam String senderName,
            @RequestParam String receiverName) {
        return new ResponseEntity<>(messageService.getMessagesBetweenUsers(senderName, receiverName), HttpStatus.OK);
    }

    @GetMapping("/get-messages-for-vendor")
    public ResponseEntity<List<Message>> getMessagesForVendor(@RequestParam String receiverName) {
        List<Message> messages = messageService.getMessagesForVendor(receiverName);
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 if no messages found
        }
        return ResponseEntity.ok(messages);
    }
}
