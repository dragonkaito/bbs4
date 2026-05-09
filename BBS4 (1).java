import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

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

    // 5桁素数探し
    static BigInteger findPrime() {

        while (true) {

            int num = 10000 + rand.nextInt(90000);

            // 偶数回避
            if (num % 2 == 0) num++;

            BigInteger candidate = BigInteger.valueOf(num);

            // mod4で3になる素数
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

        // x^((p-1)/2) mod p
        BigInteger checkP =
            x.modPow(
                p.subtract(one).divide(BigInteger.TWO),
                p);

        // x^((q-1)/2) mod q
        BigInteger checkQ =
            x.modPow(
                q.subtract(one).divide(BigInteger.TWO),
                q);

        return checkP.equals(one) &&
               checkQ.equals(one);
    }

    // seed生成（平方剰余条件追加）
    static BigInteger makeSeed(
            BigInteger n,
            BigInteger p,
            BigInteger q) {

        while (true) {

            BigInteger seed =
                new BigInteger(n.bitLength() - 1, rand);

            // gcd(seed, n)=1
            if (seed.compareTo(BigInteger.TWO) > 0 &&
                seed.compareTo(n) < 0 &&
                seed.gcd(n).equals(BigInteger.ONE)) {

                // 平方剰余条件確認
                if (isQuadraticResidue(seed, p, q)) {
                    return seed;
                }
            }
        }
    }

    // 真の周期測定
    static long detectPeriod(
            BigInteger seed,
            BigInteger n) {

        BigInteger x = seed;

        long count = 0;
        long maxCount = 1000000;

        do {

            x = x.multiply(x).mod(n);

            count++;

            if (count > maxCount) {
                return -1;
            }

        } while (!x.equals(seed));

        return count;
    }

    public static void main(String[] args)
            throws Exception {

        long startTime =
            System.currentTimeMillis();

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
            new PrintWriter(
                new FileWriter("bbs2_risk_java.csv"));

        long maxPeriod = Long.MIN_VALUE;
        long minPeriod = Long.MAX_VALUE;

        for (int i = 0; i < 1000; i++) {

            BigInteger seed =
                makeSeed(n, p, q);

            long period =
                detectPeriod(seed, n);

            pw.println(seed + "," + period);

            // 最大・最小更新
            if (period != -1) {

                if (period > maxPeriod) {
                    maxPeriod = period;
                }

                if (period < minPeriod) {
                    minPeriod = period;
                }
            }

            if ((i + 1) % 100 == 0) {
                System.out.println(
                    (i + 1) + "件処理完了");
            }
        }

        pw.close();

        long endTime =
            System.currentTimeMillis();

        long duration =
            endTime - startTime;

        System.out.println("1000件保存完了");

        System.out.println(
            "最大周期 = " + maxPeriod);

        System.out.println(
            "最小周期 = " + minPeriod);

        System.out.println(
            "実行時間: " +
            (duration / 1000.0) + "秒 (" +
            (duration / 60000.0) + "分)");
    }
}