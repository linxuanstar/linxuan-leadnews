package com.linxuan.mongo;

import com.linxuan.mongo.pojo.ApAssociateWords;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoDBApplication.class)
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    // 查询一个
    @Test
    public void saveFindOne() {
        ApAssociateWords apAssociateWords = mongoTemplate
                .findById("60bdc02a4582bc47e1137a39", ApAssociateWords.class);
        System.out.println(apAssociateWords);
    }

    // 保存
    @Test
    public void saveTest() {
        ApAssociateWords apAssociateWords = new ApAssociateWords();
        apAssociateWords.setAssociateWords("林炫直播");
        apAssociateWords.setCreatedTime(new Date());
        mongoTemplate.save(apAssociateWords);
    }

    // 条件查询
    @Test
    public void testQuery() {
        Query query = Query.query(Criteria.where("associateWords").is("黑马头条"))
                .with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<ApAssociateWords> apAssociateWordsList = mongoTemplate.find(query, ApAssociateWords.class);
        System.out.println(apAssociateWordsList);
    }

    @Test
    public void testDel() {
        mongoTemplate.remove(Query.query(Criteria.where("associateWords")
                .is("黑马头条")), ApAssociateWords.class);
    }
}
