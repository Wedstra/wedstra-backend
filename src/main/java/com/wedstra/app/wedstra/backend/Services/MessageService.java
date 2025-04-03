package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Message;
import com.wedstra.app.wedstra.backend.Repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    public Message uploadMessage(Message message) {
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
}
