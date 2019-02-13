import com.google.common.collect.ArrayListMultimap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public enum ByteCodeAdventure {
  Instance;

  private static final Logger logger = LogManager.getLogger(ByteCodeAdventure.class);
  private final ArrayListMultimap<Integer, Tenant> keyDistribution;
  private final Long2ObjectOpenHashMap<String> nameLookup;
  private final StampedLock lock;

  ByteCodeAdventure() {
    nameLookup = new Long2ObjectOpenHashMap<>();
    keyDistribution = ArrayListMultimap.create();
    lock = new StampedLock();
  }

  private <T> T read(Supplier<T> supplier) {
    long stamp = lock.tryOptimisticRead();
    T t = supplier.get();
    if (!lock.validate(stamp)) {
      stamp = lock.readLock();
      try {
        t = supplier.get();
      } finally {
        lock.unlockRead(stamp);
      }
    }

    return t;
  }

  private boolean readBoolean(BooleanSupplier supplier) {
    long stamp = lock.tryOptimisticRead();
    boolean t = supplier.getAsBoolean();
    if (!lock.validate(stamp)) {
      stamp = lock.readLock();
      try {
        t = supplier.getAsBoolean();
      } finally {
        lock.unlockRead(stamp);
      }
    }

    return t;
  }

  private static boolean contains(Long2ObjectOpenHashMap<String> table, String tenantId) {
    return table.containsValue(tenantId);
  }

  public boolean isTenantBeingProcessed(String tenantId) {
    boolean result = readBoolean(() -> nameLookup.values().contains(tenantId));
    logger.info("Current tenants: {}, query: {}, result: {}", nameLookup, tenantId, result);
    return result;
  }

  public boolean worst(String tenantId) {
    logger.info("Current tenants: {}, query: {}", nameLookup, tenantId);
    Boolean read = read(() -> contains(nameLookup, tenantId));
    logger.info("Current tenants: {}, query: {}, result: {}", nameLookup, tenantId, read);
    return read;
  }

  public boolean worst2(String tenantId) {
    logger.info("Current tenants: {}, query: {}", nameLookup, tenantId);
    Boolean read = read(() -> nameLookup.values().contains(tenantId));
    logger.info("Current tenants: {}, query: {}, result: {}", nameLookup, tenantId, read);
    return read;
  }

  public boolean intermediate(String tenantId) {
    logger.info("Current tenants: {}, query: {}", nameLookup, tenantId);
    boolean read = readBoolean(() -> contains(nameLookup, tenantId));
    logger.info("Current tenants: {}, query: {}, result: {}", nameLookup, tenantId, read);
    return read;
  }

  public boolean intermediate2(String tenantId) {
    logger.info("Current tenants: {}, query: {}", nameLookup, tenantId);
    boolean read = readBoolean(() -> nameLookup.values().contains(tenantId));
    logger.info("Current tenants: {}, query: {}, result: {}", nameLookup, tenantId, read);
    return read;
  }

  public boolean intermediate3(String tenantId) {
    boolean read = readBoolean(() -> contains(nameLookup, tenantId));
    logger.info("Current tenants: {}, query: {}, result: {}", nameLookup, tenantId, read);
    return read;
  }

  public boolean intermediate4(String tenantId) {
    boolean read = readBoolean(() -> nameLookup.values().contains(tenantId));
    logger.info("Current tenants: {}, query: {}, result: {}", nameLookup, tenantId, read);
    return read;
  }

  public boolean best(String tenantId) {
    return readBoolean(() -> nameLookup.values().contains(tenantId));
  }

  public boolean best2(String tenantId) {
    return readBoolean(() -> contains(nameLookup, tenantId));
  }

  public List<Tenant> tenantsBeingProcessed() {
    return read(() -> new ArrayList<>(keyDistribution.values()));
  }

  public ArrayListMultimap<Integer, Tenant> tenantDistribution() {
    return read(() -> ArrayListMultimap.create(keyDistribution));
  }

}
