public class Operations {

    public static  ElementFields add(ElementFields f,ElementFields g){
        ElementFields a=new ElementFields();
        for (int i = 0; i < ElementFields.el; i++) {
            long w = f.getElem(i) ^ g.getElem(i);
            a.setElem(i,w);
        }
        return a;
    }

    public static ElementFields sq(ElementFields f) {
        ElementFields res = new ElementFields();
        for (int i = 0; i < ElementFields.m; i++) {
            if (f.getBit(i)) {
                addReduced(res, 2 * i);
            }
        }
        return res;
    }
    private static void addReduced(ElementFields r, int k) {
        if (k < ElementFields.m) {
            r.changeBit(k);
            return;
        }
        int t = k - ElementFields.m;
        addReduced(r, t);
        addReduced(r, t + 1);
        addReduced(r, t + 2);
        addReduced(r, t + 15);
    }

    public static ElementFields mul(ElementFields f, ElementFields g) {
        ElementFields fg = new ElementFields();
        for (int i = 0; i < ElementFields.m; i++) {
            if (f.getBit(i)) {
                for (int j = 0; j < ElementFields.m; j++) {
                    if (g.getBit(j)) {
                        addReduced(fg, i + j);
                    }
                }
            }
        }
        return fg;
    }


    public static ElementFields inverseElMul(ElementFields f) {
        if (f.findDegree() == -1)
            throw new ArithmeticException("zero has no inverse");
        ElementFields inverse = new ElementFields(f);
        for (int i = 1; i < ElementFields.m - 1; i++) {
            inverse = sq(inverse);
            inverse = mul(inverse, f);
        }
        inverse = sq(inverse);
        return inverse;
    }

    public static int trace(ElementFields f) {
        ElementFields tmp = new ElementFields(f);
        ElementFields tr  = new ElementFields(f);

        for (int i = 1; i < ElementFields.m; i++) {
            tmp = sq(tmp);
            tr = add(tr, tmp);
        }
        return tr.getBit(0) ? 1 : 0;
    }

    public static ElementFields powHex(ElementFields a, String hex) {
        ElementFields res = ElementFields.one();
        for (char c : hex.toUpperCase().toCharArray()) {
            int v = Character.digit(c, 16);
            if (v < 0) {
                throw new IllegalArgumentException("invalid hex digit: " + c);
            }
            for (int i = 3; i >= 0; i--) {
                res = sq(res);
                if (((v >> i) & 1) == 1) {
                    res = mul(res, a);
                }
            }
        }
        return res;
    }


}




