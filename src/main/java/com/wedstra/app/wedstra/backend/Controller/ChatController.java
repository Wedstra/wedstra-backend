package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.MarkAsReadRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Message uploadedMessage = messageService.uploadMessage(message);
        System.out.println(uploadedMessage);
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

    @MessageMapping("/mark-as-read")
    public void markMessagesAsRead(@Payload MarkAsReadRequest request) {
        // Ensure the request is valid
        if (request.getMessageIds() == null || request.getMessageIds().isEmpty()) {
            return; // Nothing to do
        }

        // Step 1: Update the database and get the updated messages back
        List<Message> updatedMessages = messageService.updateReadStatus(request.getMessageIds());

        // Step 2: Notify the original sender that the messages have been read
        if (!updatedMessages.isEmpty()) {
            // All messages in this batch were sent by the same person to the current user.
            // So we can get the sender's ID from the first message.
            String originalSenderName = updatedMessages.get(0).getSenderName();

            // The receiver is the one who sent this "mark-as-read" request.
            // We can get their name from the message as well.
            String receiverName = updatedMessages.get(0).getReceiverName();

            // Prepare a meaningful payload for the notification
            Map<String, Object> payload = new HashMap<>();
            payload.put("messageIds", request.getMessageIds());
            payload.put("readBy", receiverName); // Useful for the sender's UI

            // Send the "messages-read" event back to the original sender's private channel
            simpMessagingTemplate.convertAndSendToUser(originalSenderName, "/queue/messages-read", payload);
        }
    }
}
