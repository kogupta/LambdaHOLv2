import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public enum CheckPartitions {
  ;

  public static void main(String[] args) {
    System.out.println("HAhaha");

    int n = 10;
    Random r = new Random();
    List<Integer> ns = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      int tenantId = 100 + r.nextInt(900);
      while (ns.contains(tenantId)) {
        tenantId = 100 + r.nextInt(900);
      }

      ns.add(tenantId);
    }

    List<Pair> pairs = ns.stream().map(Pair::of).collect(toList());
    partition(4, pairs);

    int[] maxPartitions = {4, 8, 16, 32};
    for (int maxPartition : maxPartitions) {
      for (int currPos = -1; currPos < maxPartition; currPos++) {
        int next = currPos;
        System.out.println("---- Start: " + next + " ----");
        for (int i = 0; i < r.nextInt(2 * maxPartition); i++) {
          System.out.print("prev: " + next);
          next = nextPartition(maxPartition, next);
          System.out.println(" next: " + next);
        }
      }
    }


  }

  private static void partition(int numPartitions, List<Pair> newKeys) {
    var stmt = new Object() {
      int row = 0;
      int tenantId;
      String mTargetGuid;
      int partition;

      public void addBatch() {
        if (row == 0) {
          System.out.println("||tenantId||partition #||");
        }

        System.out.printf("|%d|%d|%n", tenantId, partition);
        row++;
      }

      public void setString(int i, String s) {
        mTargetGuid = s;
      }

      public void setInt(int i, int n) {
        if (i == 1) tenantId = n;
        else partition = n;
      }

    };
    for (List<Pair> keys : Lists.partition(newKeys, numPartitions)) {
      assert keys.size() <= numPartitions;

      for (int i = 0, partition = 0; i < keys.size(); i++, partition++) {
        Pair pair = keys.get(i);
        stmt.setInt(1, pair.tenantId);
        stmt.setString(2, pair.mTargetGuid);
        stmt.setInt(3, partition);

        stmt.addBatch();
      }
    }

  }

  private static final class Pair {
    final int tenantId;
    final String mTargetGuid;

    private Pair(int tenantId, String mTargetGuid) {
      this.tenantId = tenantId;
      this.mTargetGuid = mTargetGuid;
    }

    public static Pair of(int n, String s) {
      return new Pair(n, s);
    }

    public static Pair of(int n) {
      return new Pair(n, "");
    }
  }

  private static int nextPartition(int maxPartitions, int currPartition) {
    Preconditions.checkArgument(currPartition >= -1 && currPartition < maxPartitions);
    // max = 16, curr = 15, expected = 0
    // max = 16, curr = 0..14, expected = curr+1
    // max = 16, curr = -1 => unassigned, start from 0, ie curr + 1
    // max = 16, curr = 17, ie # of partitions reduced => error
    return currPartition + 1 == maxPartitions ? 0 : currPartition + 1;
  }

}
