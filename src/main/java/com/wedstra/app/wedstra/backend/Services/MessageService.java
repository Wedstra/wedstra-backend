package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Message;
import com.wedstra.app.wedstra.backend.Repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    public Message uploadMessage(Message message) {
        message.setRead(false);

        // ADD THIS LINE FOR DEBUGGING
        System.out.println("--- DEBUG: Preparing to save message. isRead status: " + message.isRead() + " ---");

        return messageRepository.save(message);
    }

    public List<Message> getMessagesBetweenUsers(String senderName, String receiverName) {
        return messageRepository.findBySenderNameAndReceiverName(senderName, receiverName);
    }

    public List<Message> getMessagesForVendor(String receiverName) {
        Query query = new Query(
                new Criteria().orOperator(
                        Criteria.where("receiverName").is(receiverName),
                        Criteria.where("senderName").is(receiverName)
                )
        );
        return mongoTemplate.find(query, Message.class);
    }

    public List<Message> updateReadStatus(List<String> messageIds) {
        // Step 1: Create a query to find all messages where '_id' is in the provided list
        Query query = new Query(Criteria.where("_id").in(messageIds));

        // Step 2: Create an update operation to set the 'isRead' field to true
        Update update = new Update().set("isRead", true);

        // Step 3: Perform the bulk update. This operation updates the documents in the DB.
        mongoTemplate.updateMulti(query, update, Message.class);

        // Step 4: Now, fetch and return the updated documents using the same query.
        // This is the crucial step that was missing.
        return mongoTemplate.find(query, Message.class);
    }
}
