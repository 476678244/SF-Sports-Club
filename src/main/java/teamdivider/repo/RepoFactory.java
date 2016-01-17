package teamdivider.repo;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

@Component
public class RepoFactory {
  private static Datastore datastore;
  
  @PostConstruct
  void init() throws UnknownHostException {
    datastore = new Morphia().mapPackage("teamdivider.entity")
        .createDatastore(new MongoClient(), SpringMongoConfig.DB);
  }
  
  @Bean
  public Datastore datastore() {
    return datastore;
  }
  
}
