package teamdivider.repo;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import teamdivider.bean.eo.SequenceId;

@Configuration
public class SequenceInitializeConfig {

  private MongoOperations mongoOperation;

  @PostConstruct
  public void init() {
    if (this.mongoOperation == null) {
      @SuppressWarnings("resource")
      ApplicationContext ctx = new AnnotationConfigApplicationContext(
          SpringMongoConfig.class);
      this.mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
    }
    this.createSequences();
  }

  private void createSequences() {
    Query query = new Query();
    List<SequenceId> sequences = this.mongoOperation.find(query,
        SequenceId.class);
    for (String sequenceName : SequenceId.sequences) {
      SequenceId sequence = new SequenceId(sequenceName, 0);
      if (!sequences.contains(sequence)) {
        System.out.println("install sequence for :" + sequenceName);
        this.mongoOperation.save(sequence);
      }
    }
  }
}
