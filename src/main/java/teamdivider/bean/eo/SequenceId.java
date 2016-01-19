package teamdivider.bean.eo;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequence")
public class SequenceId {
  
  public static final String SEQUENCE_USER = "user";
  
  public static final String SEQUENCE_TYPE = "type";

  public static final String SEQUENCE_EVENT = "event";
  
  public static final String SEQUENCE_DRIVER_PASSENGER = "driver_passenger";
  
  public static final String SEQUENCE_EVENT_DRIVER = "event_driver";
  
  public static final String SEQUENCE_EVENT_FENDUI = "event_fendui";
  
  public static final String SEQUENCE_EVENT_MEMBER = "event_member";
  
  public static final String SEQUENCE_TYPE_EVENT = "type_event";
  
  public static final String SEQUENCE_TYPE_ORGANIZER = "type_organizer";
  
  public static final String SEQUENCE_TYPE_SUBSCRIBER = "type_subscriber";
  
  public static final Set<String> sequences = new HashSet<String>();
  
  static {
    sequences.add(SEQUENCE_USER);
    sequences.add(SEQUENCE_TYPE);
    sequences.add(SEQUENCE_EVENT);
    sequences.add(SEQUENCE_DRIVER_PASSENGER);
    sequences.add(SEQUENCE_EVENT_DRIVER);
    sequences.add(SEQUENCE_EVENT_FENDUI);
    sequences.add(SEQUENCE_EVENT_MEMBER);
    sequences.add(SEQUENCE_TYPE_EVENT);
    sequences.add(SEQUENCE_TYPE_ORGANIZER);
    sequences.add(SEQUENCE_TYPE_SUBSCRIBER);
  }

  @Id
  private String id;

  private long seq;

  public SequenceId() {}

  public SequenceId(String id, long seq) {
    super();
    this.id = id;
    this.seq = seq;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getSeq() {
    return seq;
  }

  public void setSeq(long seq) {
    this.seq = seq;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SequenceId other = (SequenceId) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}