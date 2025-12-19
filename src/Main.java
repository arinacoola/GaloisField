import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ElementFields a = fromHex("77cec6a14dc84e14795a9f0fd6cbfdefa28baa356c043d39b6243060a349");
        ElementFields b = fromHex("65fad64fd57003ab9e395d07652265eac9a9b6d480ee7548513cda6a2a7d");

         ElementFields add = Operations.add(a, b);
        System.out.println("A + B = " + hex(add));
        checkEq("Add", add, Operations.add(b, a));

        ElementFields mul = Operations.mul(a, b);
        System.out.println("A * B = " + hex(mul));
        checkEq("Mul", mul, Operations.mul(b, a));

        ElementFields sq = Operations.sq(a);
        System.out.println("A^2 = " + hex(sq));
        checkEq("Square A^2 = A*A", sq, Operations.mul(a, a));

        ElementFields inv = Operations.inverseElMul(a);
        System.out.println("A^-1 = " + hex(inv));
        checkEq("Inverse A*A^-1", Operations.mul(a, inv), ElementFields.one());

        String cHex = "322170b85e5a828e1cbaaf0d80cbf3222900960b9f7bb1f0dc58706c5cd0";

        ElementFields powAC = Operations.powHex(a, cHex);
        System.out.println("A^C = " + hex(powAC));
        checkEq("PowHex A^1 = A", Operations.powHex(a, "1"), a);


        int tr = Operations.trace(a);
        System.out.println("Trace(A) = " + tr);
        System.out.println("Trace in GF(2) : " + (tr == 0 || tr == 1 ? "YES" : "NO"));


        int launchOp = 1000;
        long tAdd = 0, tMul = 0, tSq = 0, tInv = 0, tPow = 0, tTrace = 0;
        ElementFields[] As = new ElementFields[launchOp];
        ElementFields[] Bs = new ElementFields[launchOp];

        for (int i = 0; i < launchOp; i++) {
            As[i] = randomGF();
            Bs[i] = randomGF();
        }

        int warm = Math.min(2000, launchOp);
        for (int i = 0; i < warm; i++) {
            Operations.add(As[i], Bs[i]);
            Operations.mul(As[i], Bs[i]);
            Operations.sq(As[i]);
            Operations.inverseElMul(As[i]);
            Operations.powHex(As[i], cHex);
            Operations.trace(As[i]);
        }

        System.gc();
        Thread.sleep(20);

        long t0 = System.nanoTime();
        for (int i = 0; i < launchOp; i++){
            Operations.add(As[i], Bs[i]);
        }
        long t1 = System.nanoTime();
        tAdd = t1 - t0;


        t0 = System.nanoTime();
        for (int i = 0; i < launchOp; i++) {
            Operations.mul(As[i], Bs[i]);
        }
        t1 = System.nanoTime();
        tMul = t1 - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < launchOp; i++) {
            Operations.sq(As[i]);
        }
        t1 = System.nanoTime();
        tSq = t1 - t0;

        int heavy = Math.max(1, launchOp / 10);

        t0 = System.nanoTime();
        for (int i = 0; i < heavy; i++){
            Operations.inverseElMul(As[i]);
        }
        t1 = System.nanoTime();
        tInv = t1 - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < heavy; i++) {
            Operations.powHex(As[i], cHex);
        }
        t1 = System.nanoTime();
        tPow = t1 - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < launchOp; i++){
            Operations.trace(As[i]);
        }
        t1 = System.nanoTime();
        tTrace = t1 - t0;

        System.out.println("\nTime measurements GF(2^239) (avg ns):");
        System.out.printf("Add:     %.2f ns%n", tAdd   / (double) launchOp);
        System.out.printf("Mul:     %.2f ns%n", tMul   / (double) launchOp);
        System.out.printf("Square:  %.2f ns%n", tSq    / (double) launchOp);
        System.out.printf("Inverse: %.2f ns%n", tInv   / (double) heavy);
        System.out.printf("Power:   %.2f ns%n", tPow   / (double) heavy);
        System.out.printf("Trace:   %.2f ns%n", tTrace / (double) launchOp);

        double freqGHz = 3.2;
        System.out.println("\ncyclles per operation:");
        System.out.printf("Add:   %.0f cycles%n", (tAdd   / (double) launchOp) * freqGHz);
        System.out.printf("Mul:    %.0f cycles%n", (tMul   / (double) launchOp) * freqGHz);
        System.out.printf("Square:  %.0f cycles%n", (tSq    / (double) launchOp) * freqGHz);
        System.out.printf("Inverse: %.0f cycles%n", (tInv   / (double) heavy)    * freqGHz);
        System.out.printf("Power:  %.0f cycles%n", (tPow   / (double) heavy)    * freqGHz);
        System.out.printf("Trace:  %.0f cycles%n", (tTrace / (double) launchOp) * freqGHz);
    }

    private static ElementFields randomGF() {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ElementFields.m; i++) {
            sb.append(rnd.nextBoolean() ? '1' : '0');
        }
        return ElementFields.fromBitString(sb.toString());
    }

    private static void checkEq(String msg, ElementFields a, ElementFields b) {
        if (!hex(a).equals(hex(b))) {
            System.out.println("NO! " + msg + ": our result " + hex(a) + " what is expected " + hex(b));
        } else {
            System.out.println("YES! " + msg);
        }
    }

    private static String hex(ElementFields e) {
        String bits = e.toBitString();
        bits = bits.replaceFirst("^0+(?!$)", "");
        int pad = (4 - bits.length() % 4) % 4;
        bits = "0".repeat(pad) + bits;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bits.length(); i += 4) {
            sb.append(Integer.toHexString(Integer.parseInt(bits.substring(i, i + 4), 2)));
        }
        return sb.toString();
    }

    private static ElementFields fromHex(String hex) {
        hex = hex.trim();
        if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        hex = hex.replaceAll("\\s+", "").toUpperCase();
        StringBuilder bin = new StringBuilder();
        for (char c : hex.toCharArray()) {
            int v = Character.digit(c, 16);
            if (v < 0) throw new IllegalArgumentException("invalid hex digit: " + c);
            bin.append(String.format("%4s", Integer.toBinaryString(v)).replace(' ', '0'));
        }
        String bits = bin.toString().replaceFirst("^0+(?!$)", "");
        if (bits.length() > ElementFields.m) {
            throw new IllegalArgumentException("hex too long for GF(2^" + ElementFields.m + ")");
        }
        int pad = ElementFields.m - bits.length();
        bits = "0".repeat(pad) + bits;

        return ElementFields.fromBitString(bits);
    }

}
