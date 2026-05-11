import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;
import java.util.HashMap;

public class BBS4 {

    static Random rand = new Random();

    // フェルマーテスト
    static boolean fermatTest(BigInteger n, int times) {

        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO)) return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;

        for (int i = 0; i < times; i++) {

            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), rand);
            } while (a.compareTo(BigInteger.TWO) < 0 ||
                     a.compareTo(n.subtract(BigInteger.ONE)) >= 0);

            if (!a.modPow(n.subtract(BigInteger.ONE), n)
                    .equals(BigInteger.ONE)) {
                return false;
            }
        }
        return true;
    }

    // 6桁素数生成（p ≡ 3 mod 4）
    static BigInteger findPrime() {

        while (true) {

            int num = 100000 + rand.nextInt(900000);

            if (num % 2 == 0) num++;

            BigInteger candidate = BigInteger.valueOf(num);

            if (candidate.mod(BigInteger.valueOf(4))
                    .equals(BigInteger.valueOf(3))
                && fermatTest(candidate, 10)) {

                return candidate;
            }
        }
    }

    // 平方剰余判定
    static boolean isQuadraticResidue(
            BigInteger x,
            BigInteger p,
            BigInteger q) {

        BigInteger one = BigInteger.ONE;

        BigInteger checkP =
            x.modPow(p.subtract(one).divide(BigInteger.TWO), p);

        BigInteger checkQ =
            x.modPow(q.subtract(one).divide(BigInteger.TWO), q);

        return checkP.equals(one) && checkQ.equals(one);
    }

    // seed生成
    static BigInteger makeSeed(
            BigInteger n,
            BigInteger p,
            BigInteger q) {

        while (true) {

            BigInteger seed =
                new BigInteger(n.bitLength() - 1, rand);

            if (seed.compareTo(BigInteger.TWO) > 0 &&
                seed.compareTo(n) < 0 &&
                seed.gcd(n).equals(BigInteger.ONE)) {

                if (isQuadraticResidue(seed, p, q)) {
                    return seed;
                }
            }
        }
    }

    // 周期検出（修正版）
    static long detectPeriod(BigInteger seed, BigInteger n) {

        BigInteger x = seed;
        HashMap<BigInteger, Long> seen = new HashMap<>();

        long count = 0;

        while (true) {

            if (seen.containsKey(x)) {
                return count - seen.get(x);
            }

            seen.put(x, count);

            x = x.multiply(x).mod(n);
            count++;

            if (count > 1_000_000) {
                return -1;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();

        BigInteger p = findPrime();
        BigInteger q = findPrime();

        while (p.equals(q)) {
            q = findPrime();
        }

        BigInteger n = p.multiply(q);

        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("n = " + n);

        PrintWriter pw =
            new PrintWriter(new FileWriter("bbs5_risk_java.csv"));

        Long maxPeriod = null;
        Long minPeriod = null;

        for (int i = 0; i < 1000; i++) {

            BigInteger seed = makeSeed(n, p, q);

            long period = detectPeriod(seed, n);

            pw.println(seed + "," + period);

            if (period != -1) {

                if (maxPeriod == null || period > maxPeriod) {
                    maxPeriod = period;
                }

                if (minPeriod == null || period < minPeriod) {
                    minPeriod = period;
                }
            }

            if ((i + 1) % 100 == 0) {
                System.out.println((i + 1) + "件処理完了");
            }
        }

        pw.close();

        long endTime = System.currentTimeMillis();

        System.out.println("1000件保存完了");

        System.out.println("最大周期 = " + maxPeriod);
        System.out.println("最小周期 = " + minPeriod);

        System.out.println(
            "実行時間: " +
            ((endTime - startTime) / 1000.0) + "秒"
        );
    }
}