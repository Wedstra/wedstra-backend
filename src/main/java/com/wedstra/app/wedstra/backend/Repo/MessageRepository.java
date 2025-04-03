package com.wedstra.app.wedstra.backend.Repo;

import com.wedstra.app.wedstra.backend.Entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> findBySenderNameAndReceiverName(String senderName, String receiverName);
}
